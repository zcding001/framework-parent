package com.yirun.framework.dao.mybatis.interceptor;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;

@Intercepts({
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class LogDaoInterceptor implements Interceptor {
	
	private static Logger logger = Logger.getLogger(LogDaoInterceptor.class);
	
	static int MAPPED_STATEMENT_INDEX = 0;
	static int PARAMETER_INDEX = 1;
	static int ROWBOUNDS_INDEX = 2;
	static int RESULT_HANDLER_INDEX = 3;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object [] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement)args[MAPPED_STATEMENT_INDEX];
		Object parameter = args[PARAMETER_INDEX];
		BoundSql boundSql = ms.getBoundSql(parameter);

		File file = new File(ms.getResource());
		String sqlMapName = file.getName();
		System.out.println("sqlMapName:"+sqlMapName);
		System.out.println("statementId:"+ms.getId());
		System.out.println("sql:"+boundSql.getSql().trim());
//		System.out.println(ms.getConfiguration());
//		System.out.println(ToStringBuilder.reflectionToString(ms.getParameterMap(),ToStringStyle.MULTI_LINE_STYLE));
//		System.out.println(ToStringBuilder.reflectionToString(boundSql,ToStringStyle.MULTI_LINE_STYLE));
//	    System.out.println(ms.getSqlSource());
 
		StringBuffer buf = new StringBuffer();
		buf.append("params[");
		List<ParameterMapping> lists = boundSql.getParameterMappings();
		for (int i =0; lists != null && i < lists.size();i++) {
			 ParameterMapping param =lists.get(i);
			 if (i == lists.size() -1) {
					buf.append(boundSql.getAdditionalParameter(param.getProperty()));
			 } else {
					buf.append(boundSql.getAdditionalParameter(param.getProperty())).append(",");
			 }
		}
		buf.append(ToStringBuilder.reflectionToString(boundSql.getParameterObject()));
		buf.append("]");
		
		System.out.println("params:"+buf.toString());
//		System.out.println(ToStringBuilder.reflectionToString(ms));
//		System.out.println(ToStringBuilder.reflectionToString(parameter));
//		System.out.println(ToStringBuilder.reflectionToString(boundSql));
 
		
		long startTime = System.currentTimeMillis();
		Object resObj =  invocation.proceed();
		long endTime = System.currentTimeMillis();
		 
		long difTime = endTime - startTime;
		logger.error(boundSql.getSql());
		logger.error("执行时间:"+difTime + " 毫秒");
		return resObj;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		 
	}

}

