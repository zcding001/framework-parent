package com.yirun.framework.dao.mybatis.interceptor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.ReflectionUtils;
//
//import cn.bidlink.framework.core.utils.ConfigUtils;
//import cn.bidlink.framework.dao.ibatis.PropertiesHelper;
//import cn.bidlink.framework.dao.ibatis.dialect.Dialect;
//import cn.bidlink.framework.dao.ibatis.resultHandler.CountResultHandler;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import com.yirun.framework.dao.mybatis.PropertiesHelper;
import com.yirun.framework.dao.mybatis.dialect.Dialect;
import com.yirun.framework.dao.mybatis.resultHandler.CountResultHandler;

/**
 * 为ibatis3提供基于方言(Dialect)的分页查询的插件
 * 将拦截Executor.query()方法实现分页方言的插入.
 * 
 * 配置文件内容:
 * <pre>
 * 	&lt;plugins>
 *		&lt;plugin interceptor="cn.org.rapid_framework.ibatis3.plugin.OffsetLimitInterceptor">
 *			&lt;property name="dialectClass" value="cn.org.rapid_framework.jdbc.dialect.MySQLDialect"/>
 *		&lt;/plugin>
 *	&lt;/plugins>
 * </pre>
 */

@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }) })
public class OffsetLimitInterceptor implements Interceptor{
	
	static int MAPPED_STATEMENT_INDEX = 0;
	static int PARAMETER_INDEX = 1;
	static int ROWBOUNDS_INDEX = 2;
	static int RESULT_HANDLER_INDEX = 3;
	
	Dialect dialect;
	
	private boolean isFilterParam = true;
	
	public Object intercept(Invocation invocation) throws Throwable {
		processIntercept(invocation.getArgs());
		return invocation.proceed();
	}

	void processIntercept(final Object[] queryArgs) {
		//queryArgs = query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler)
		MappedStatement ms = (MappedStatement)queryArgs[MAPPED_STATEMENT_INDEX];

		Object parameter = queryArgs[PARAMETER_INDEX];

 		if(isFilterParam) {
			parameter = sqlFilter(parameter);
		}
	
		final RowBounds rowBounds = (RowBounds)queryArgs[ROWBOUNDS_INDEX];
 
		int offset = rowBounds.getOffset();
		int limit = rowBounds.getLimit();
		if(dialect.supportsLimit() &&  ( offset != RowBounds.NO_ROW_OFFSET || limit != RowBounds.NO_ROW_LIMIT)  )  {
			BoundSql boundSql = ms.getBoundSql(parameter);			
			String sql = boundSql.getSql().trim();
			//根据方言，自动生成对应的sql语句 
			sql = dialect.getLimitString(sql, offset, limit);
			//分页查询变量为默认，目的似的mybatis的自己分页策略不执行，mybatis是把数据全部取回，然后在用for循环分页 
			queryArgs[ROWBOUNDS_INDEX] = new RowBounds();

			//生成新的sql绑定
			BoundSql newBoundSql = new BoundSql(ms.getConfiguration(),sql, boundSql.getParameterMappings(), boundSql.getParameterObject());

			//获得BoundSql中的additionalParameters参数值
			ReflectionUtils.doWithFields(BoundSql.class, new FieldCallback() {
				@SuppressWarnings("unchecked")
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					field.setAccessible(true);
					((Map<String, Object>)(field.get(boundSql))).entrySet().forEach(e -> newBoundSql.setAdditionalParameter(e.getKey(), e.getValue()));
					field.setAccessible(false);
				}
			}, new FieldFilter() {
				@Override
				public boolean matches(Field field) {
					return "additionalParameters".equals(field.getName());
				}
			});
			
			//生成新的绑定mapper对象，新的对象实际是分页的 mapper
			MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql),null);

			queryArgs[MAPPED_STATEMENT_INDEX] = newMs;	
		}else if (queryArgs.length>=4 && queryArgs[RESULT_HANDLER_INDEX]!=null ){
			Object resultHandler = queryArgs[RESULT_HANDLER_INDEX];
			//查看是否是自定义的总条数结果处理类
			if(resultHandler instanceof CountResultHandler){
				//开始重新拼装sql
				BoundSql boundSql = ms.getBoundSql(parameter);	
				String sql = boundSql.getSql().trim();
				//生成带有 select count(*) from (....)的语句
				sql = dialect.getCountString(sql);
				queryArgs[ROWBOUNDS_INDEX] = new RowBounds();
 
				//生成新的sql绑定
				BoundSql newBoundSql = new BoundSql(ms.getConfiguration(),sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
				//生成返回类型 查找总条数的用long类型返回 
				ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(ms.getConfiguration(), ms.getId()
						+ "-InlineCount.RestulMapper", Integer.class, new ArrayList<ResultMapping>());
				ResultMap rm =inlineResultMapBuilder.build();
				List<ResultMap> list =   new ArrayList<ResultMap>();
				list.add(rm);
				
				//生成新的绑定mapper对象，新的对象实际是分页的 mapper
				MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql),list);

				queryArgs[MAPPED_STATEMENT_INDEX] = newMs;
			}
		}
	}	
	
	//see: MapperBuilderAssistant
	private MappedStatement copyFromMappedStatement(MappedStatement ms,SqlSource newSqlSource,List<ResultMap> resultMap) {
		Builder builder = new MappedStatement.Builder(ms.getConfiguration(),ms.getId(),newSqlSource,ms.getSqlCommandType());
 
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());		
		builder.timeout(ms.getTimeout());
		//setStatementResultMap()
 
		builder.parameterMap(ms.getParameterMap());		
		//setStatementResultMap()
		if(resultMap==null){//有自定义的映射结果集就用自己的，否则还用原来配置文件中的 
			builder.resultMaps(ms.getResultMaps());
		}else{
			builder.resultMaps(resultMap);
		}
		builder.resultSetType(ms.getResultSetType());
		//setStatementCache()
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());
 
		return builder.build();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
		String dialectClass = new PropertiesHelper(properties).getRequiredString("dialectClass");
		try {
			dialect = (Dialect)Class.forName(dialectClass).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("cannot create dialect instance by dialectClass:"+dialectClass,e);
		} 
	}
	
	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;
		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}
		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}
	
	/**
	 *  @Description    : SQL 过滤
	 *  @Method_Name    : sqlFilter
	 *  @param parameter
	 *  @return         : Object
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	@SuppressWarnings("unchecked")
	private Object sqlFilter(Object parameter) {
		try {
			Class<?> paramType = (parameter == null || parameter.getClass() == null) ? Object.class : parameter
					.getClass();
			if (paramType != Object.class && !(parameter instanceof Map)) {
				Field[] fields = paramType.getDeclaredFields();
				for (Field field2 : fields) {
					if (field2.getType() == String.class) {
						Field field = FieldUtils.getDeclaredField(parameter.getClass(), field2.getName(), true);

						Object obj = field.get(parameter);
						if (obj != null) {
							String paramStr = String.valueOf(obj.toString());
							if (paramStr.trim().contains("%")) {
								paramStr = paramStr.replaceAll("%", "\\\\%");
								field.set(parameter, paramStr);
							}
							if (paramStr.trim().contains("'")) {
								paramStr = paramStr.replaceAll("'", "\\\\'");
								field.set(parameter, paramStr);
							}
						}
					}
				}
			} else if (parameter != null && parameter instanceof Map) {
				HashMap<String, Object> paramMap = (HashMap<String, Object>) parameter;
				for (Iterator<Map.Entry<String, Object>> it = paramMap.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, Object> entry = it.next();
					String key = entry.getKey();
					Object obj = entry.getValue();
					if (obj instanceof String) {
						String par = obj.toString();
						if (par.trim().contains("%")) {
							par = par.replaceAll("%", "\\\\%");
							paramMap.put(key, par);
						}
						if (par.trim().contains("'")) {
							par = par.replaceAll("'", "\\\\'");
							paramMap.put(key, par);
						}
					}
				}

			} else if (parameter != null && parameter instanceof String) {
				String str = (String) parameter;
				if (str.trim().contains("%")) {
					str = str.replaceAll("%", "\\\\%");
				}
				if (str.trim().contains("'")) {
					str = str.replaceAll("'", "\\\\'");
				}
				parameter = str;
			}
		} catch (Exception e) {
			ReflectionUtils.handleReflectionException(e);
		}
		return parameter;
	}

	public boolean isFilterParam() {
		return isFilterParam;
	}

	public void setFilterParam(boolean isFilterParam) {
		this.isFilterParam = isFilterParam;
	}
}
