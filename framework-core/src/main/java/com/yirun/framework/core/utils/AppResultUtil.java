package com.yirun.framework.core.utils;

import com.yirun.framework.core.model.ResponseEntity;
import com.yirun.framework.core.utils.pager.Pager;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.yirun.framework.core.commons.Constants.ERROR;
import static com.yirun.framework.core.commons.Constants.SUCCESS;

/**
 * App返回结果工具
 * 
 * @author :zhongpingtang
 */
public class AppResultUtil {

	/**
	 * 简单的只包含成功状态的Map
	 */
	public static final Map<String, Object> SUCCESS_MAP;
	/**
	 * 简单的只包含失败状态的Map
	 */
	public static final Map<String, Object> ERROR_MAP;
	public static final String DATA_LIST = "dataList";

	/**
	 * 保存当前线程的处理类型
	 */
	private static ThreadLocal<List<ProcessCondition>> processConditionThreadLocal
			= ThreadLocal.withInitial(() -> new ArrayList<>());

	static {
		// 初始化map
		SUCCESS_MAP = createSimpleMap(true);
		ERROR_MAP = createSimpleMap(false);
	}

	/**
	 * 产生一个ExtendMap
	 *
	 * @return
	 */
	private static ExtendMap getExtendMap(Integer initialCapacity) {
		ExtendMap extendMap = new ExtendMap(initialCapacity);
		if (processConditionThreadLocal.get()!=null) {
			//传递处理函数
			extendMap.setProcessConditions(processConditionThreadLocal.get());
			processConditionThreadLocal.remove();
		}
		return extendMap;
	}

	/**
	 * 将ResponseEntity转化为ExtendMap
	 * 
	 * @param responseEntity
	 * @param ignoreProperties
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfResponseEntity(ResponseEntity<T> responseEntity,String... ignoreProperties) {
		if (responseEntity == null) {
			throw new IllegalStateException("ResponseEntity cna't be null");
		}
		ExtendMap extendMap = getExtendMap(16);
		initCommon(extendMap, responseEntity.getResStatus(), responseEntity.getResMsg());
		Map<String, Object> params = responseEntity.getParams();
		if (!MapUtils.isEmpty(params)) {
			params.forEach((key, value) -> {
				if (!ArrayUtils.contains(ignoreProperties, key)) {
					extendMap.put(key, value);
				}
			});
		}
		return extendMap;
	}
	/**
	*  @Description    ：对map里面的对象处理
	*  @Method_Name    ：mapOfObjectInPropertiesNullable
	*  @param source
	*  @param resStatus
	*  @param resMsg
	*  @param nullable
	*  @param ignoreProperties
	*  @return com.yirun.framework.core.utils.AppResultUtil.ExtendMap
	*  @Creation Date  ：2018/5/15
	*  @Author         ：zhongpingtang@hongkun.com.cn
	*/
	public static <T> ExtendMap mapOfObjectInPropertiesNullable(Map<String,Object> source,Integer resStatus, Object resMsg, 
			boolean nullable,String... ignoreProperties) {
		if (source == null) {
			throw new IllegalStateException("source cna't be null");
		}
		ExtendMap extendMap = getExtendMap(16);
		initCommon(extendMap, resStatus,resMsg);
		if (!MapUtils.isEmpty(source)) {
			source.forEach((key, value) -> {
				 extendMap.put(key, mapOfNullable(value,nullable,ignoreProperties));
			});
		}
		return extendMap;
	}
	/**
	 * 将Pager对象转化为返回值
	 * 
	 * @param pager
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfPager(Pager pager) {
		return successOfPager(pager, null, null);
	}

	/**
	 * 将Pager对象转化为返回值
	 * 
	 * @param pager
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfPager(Pager pager, String... ignoreProperties) {
		return successOfPager(pager, null, ignoreProperties);
	}

	/**
	 * 将Pager对象转化为返回值
	 * 
	 * @param pager
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfPager(Pager pager, Object resMsg, String... ignoreProperties) {
		if (pager == null) {
			throw new IllegalStateException("pager cna't be null");
		}
		return successOfList(pager.getData(), resMsg, ignoreProperties);
	}

	/**
	 * 将Pager对象转化为返回值
	 * 
	 * @param pager
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfPagerInProperties(Pager pager, String... inProperties) {
		return successOfPagerInProperties(pager, null, inProperties);
	}

	/**
	 * 将Pager对象转化为返回值
	 * 
	 * @param pager
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfPagerInProperties(Pager pager, Object resMsg, String... inProperties) {
		if (pager == null) {
			throw new IllegalStateException("pager cna't be null");
		}
		return successOfListInProperties(pager.getData(), resMsg, inProperties);
	}

	/**
	 * 将对象装换为map,默认允许属性值为null
	 *
	 * @param source
	 *            源对象
	 * @param resStatus
	 *            返回状态码
	 * @param resMsg
	 *            返回信息
	 * @param ignoreProperties
	 *            忽略属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOf(T source, Integer resStatus, Object resMsg, String... ignoreProperties) {
		return mapOfNullable(source, resStatus, resMsg, true, ignoreProperties);
	}

	/**
	 * 返回成功时候的简便方法，不带信息
	 *
	 * @param source
	 *            源对象
	 * @param ignoreProperties
	 *            忽略属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOf(T source, String... ignoreProperties) {
		return mapOfNullable(source, SUCCESS, null, true, ignoreProperties);
	}

	/**
	 * 返回成功时候的简便方法-带信息
	 *
	 * @param source
	 *            源对象
	 * @param resMsg
	 *            返回信息
	 * @param ignoreProperties
	 *            忽略属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOf(T source, Object resMsg, String... ignoreProperties) {
		return mapOfNullable(source, SUCCESS, resMsg, true, ignoreProperties);
	}

	/**
	 * 返回失败时候的简便方法-不带信息
	 *
	 * @param source
	 *            源对象
	 * @param ignoreProperties
	 *            忽略属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap errorOf(T source, String... ignoreProperties) {
		return mapOfNullable(source, ERROR, null, true, ignoreProperties);
	}

	/**
	 * 返回失败时候的简便方法-带信息
	 *
	 * @param source
	 *            源对象
	 * @param resMsg
	 *            返回信息
	 * @param ignoreProperties
	 *            忽略属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap errorOf(T source, Object resMsg, String... ignoreProperties) {
		return mapOfNullable(source, ERROR, resMsg, true, ignoreProperties);
	}

	/**
	 * 将对象装换为map,只包含指定的属性，默认允许为null
	 *
	 * @param source
	 *            源对象
	 * @param resStatus
	 *            返回状态码
	 * @param resMsg
	 *            返回信息
	 * @param includeProperties
	 *            只被包含的属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfInProperties(T source, Integer resStatus, Object resMsg,
			String... includeProperties) {
		return mapOfInPropertiesNullable(source, resStatus, resMsg, true, includeProperties);
	}

	/**
	 * 返回成功时候的简便方法-带信息
	 *
	 * @param source
	 *            源对象
	 * @param resMsg
	 *            返回信息
	 * @param includeProperties
	 *            只被包含的属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfInProperties(T source, Object resMsg, String... includeProperties) {
		return mapOfInPropertiesNullable(source, SUCCESS, resMsg, true, includeProperties);
	}

	/**
	 * 返回成功时候的简便方法-不带信息
	 *
	 * @param source
	 *            源对象
	 * @param includeProperties
	 *            只被包含的属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfInProperties(T source, String... includeProperties) {
		return mapOfInPropertiesNullable(source, SUCCESS, null, true, includeProperties);
	}

	/**
	 * 返回失败时候的简便方法--带信息
	 *
	 * @param source
	 *            源对象
	 * @param resMsg
	 *            返回信息
	 * @param includeProperties
	 *            只被包含的属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap errorOfInProperties(T source, Object resMsg, String... includeProperties) {
		return mapOfInPropertiesNullable(source, ERROR, resMsg, true, includeProperties);
	}

	/**
	 * 返回失败时候的简便方法-不带信息
	 *
	 * @param source
	 *            源对象
	 * @param includeProperties
	 *            只被包含的属性
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap errorOfInProperties(T source, String... includeProperties) {
		return mapOfInPropertiesNullable(source, ERROR, null, true, includeProperties);
	}

	/**
	 * 将对象装换为map，可指定是否为null，可指定忽略属性
	 *
	 * @param source
	 *            源对象
	 * @param resStatus
	 *            返回状态码
	 * @param resMsg
	 *            返回信息
	 * @param ignoreProperties
	 *            忽略属性
	 * @param nullable
	 *            是否允许为null的属性存在
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfNullable(T source, Integer resStatus, Object resMsg, boolean nullable,
			String... ignoreProperties) {
		ExtendMap resultMap = getExtendMap(16);
		initCommon(resultMap, resStatus, resMsg);
		fullBeanMap(source, resultMap,
				(propertyName, propertyValue) -> (!ArrayUtils.contains(ignoreProperties, propertyName))
						&& (nullable ? true : (propertyValue != null)));
		return resultMap;
	}

	/***
	*  @Description    ：将对象装换为map，可指定是否为null，可指定忽略属性
	*  @Method_Name    ：mapOfNullable
	*  @param source
	*  @param nullable
	*  @param includeProperties
	*  @return com.yirun.framework.core.utils.AppResultUtil.ExtendMap
	*  @Creation Date  ：2018/5/15
	*  @Author         ：zhongpingtang@hongkun.com.cn
	*/
	public static <T> ExtendMap mapOfNullable(T source, boolean nullable,
			String... includeProperties) {
		ExtendMap resultMap = getExtendMap(16);
		fullBeanMap(source, resultMap,
				(propertyName, propertyValue) -> (ArrayUtils.contains(includeProperties, propertyName))
						&& (nullable ? true : (propertyValue != null)));
		return resultMap;
	}
	/**
	 * * 将对象装换为map，可指定是否为null，可指定只包含属性
	 *
	 * @param source
	 *            源对象
	 * @param resStatus
	 *            返回状态码
	 * @param resMsg
	 *            返回信息
	 * @param includeProperties
	 *            只被包含的属性
	 * @param nullable
	 *            是否允许为null
	 * @param includeProperties
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfInPropertiesNullable(T source, Integer resStatus, Object resMsg, boolean nullable,
			String... includeProperties) {
		ExtendMap resultMap = getExtendMap(16);
		initCommon(resultMap, resStatus, resMsg);
		fullBeanMap(source, resultMap,
				(propertyName, propertyValue) -> ArrayUtils.contains(includeProperties, propertyName)
						&& (nullable ? true : (propertyValue != null)));
		return resultMap;
	}

	/**
	 * 将List<T>转换为List<ExtendMap<String, Object>>，可指定忽略属性， 默认允许属性为null,带信息
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfList(List<T> objList, Integer resStatus, Object resMsg,
			String... ignoreProperties) {
		return mapOfListNullable(objList, resStatus, resMsg, true, ignoreProperties);
	}

	/**
	 * 将List<T>转换为List<ExtendMap<String, Object>>，可指定忽略属性， 默认允许属性为null,不带信息
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfList(List<T> objList, Integer resStatus, String... ignoreProperties) {
		return mapOfListNullable(objList, resStatus, null, true, ignoreProperties);
	}

	/**
	 * 返回成功的简便方法 将List<T>转换为List<ExtendMap<String, Object>>，可指定忽略属性， 默认允许属性为null
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfList(List<T> objList, Object resMsg, String... ignoreProperties) {
		return mapOfListNullable(objList, SUCCESS, resMsg, true, ignoreProperties);
	}

	/**
	*  @Description    ：需要增加对key或者类型处理的处理器,
	 *                   只要条件不为空,必须满足不为空的条件的才进行处理
	*  @Method_Name    ：addTransfer
	*  @param keyName 处理属性的名称
	*  @param type   处理属性类型的名称
	*  @param function    处理逻辑
	*  @return void
	*  @Creation Date  ：2018/5/14
	*  @Author         ：zhongpingtang@hongkun.com.cn
	*/
	public static <A,B>  void addTransfer(String keyName,Object value,Class<A> type,Function<A,B> function){
		ProcessCondition pc = new ProcessCondition(keyName, value, type, function);
		if (pc.isEmpty()) {
			throw new IllegalStateException("不能确定处理对象的类型!");
		}
		processConditionThreadLocal.get().add(pc);
	}

	/**
	 * 返回成功的简便方法 将List<T>转换为List<ExtendMap<String, Object>>，可指定忽略属性，
	 * 默认允许属性为null,不带信息
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfList(List<T> objList, String... ignoreProperties) {
		return mapOfListNullable(objList, SUCCESS, null, true, ignoreProperties);
	}

	/**
	 * 返回失败的简便方法-带信息 将List<T>转换为List<ExtendMap<String, Object>>，可指定忽略属性，
	 * 默认允许属性为null
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap errorOfList(List<T> objList, Object resMsg, String... ignoreProperties) {
		return mapOfListNullable(objList, ERROR, resMsg, true, ignoreProperties);
	}

	/**
	 * 返回失败的简便方法 将List<T>转换为List<ExtendMap<String, Object>>，可指定忽略属性，
	 * 默认允许属性为null,不带信息
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap errorOfList(List<T> objList, String... ignoreProperties) {
		return mapOfListNullable(objList, ERROR, null, true, ignoreProperties);
	}

	/**
	 * 将List<T>转换为List<ExtendMap<String, Object>> ，可指定忽略属性，可指定属性是否可以为null
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfListNullable(List<T> objList, Integer resStatus, Object resMsg, boolean nullable,
			String... ignoreProperties) {
		return ergodicListObject(objList, resStatus, resMsg,
				(propertyName, propertyValue) -> (!ArrayUtils.contains(ignoreProperties, propertyName)
						&& (nullable ? true : (propertyValue != null))));
	}

	/**
	 * 将List<T>转换为List<ExtendMap<String, Object>>，可指定只包含的属性，默认允许属性为null
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfListInProperties(List<T> objList, Integer resStatus, Object resMsg,
			String... includeProperties) {
		return mapOfListInPropertiesNullable(objList, resStatus, resMsg, true, includeProperties);
	}

	/**
	 * 返回成功的简便方法 将List<T>转换为List<ExtendMap<String,
	 * Object>>，可指定只包含的属性，默认允许属性为null
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap successOfListInProperties(List<T> objList, Object resMsg, String... includeProperties) {
		return mapOfListInPropertiesNullable(objList, SUCCESS, resMsg, true, includeProperties);
	}

	/**
	 * 返回失败的简便方法 将List<T>转换为List<ExtendMap<String,
	 * Object>>，可指定只包含的属性，默认允许属性为null
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap errorOfListInProperties(List<T> objList, Object resMsg, String... includeProperties) {
		return mapOfListInPropertiesNullable(objList, ERROR, resMsg, true, includeProperties);
	}

	/**
	 * 将List<T>转换为List<ExtendMap<String, Object>> ，可指定只包含的属性，可指定属性是否允许为null
	 *
	 * @param objList
	 *            对象的list
	 * @param <T>
	 * @return
	 */
	public static <T> ExtendMap mapOfListInPropertiesNullable(List<T> objList, Integer resStatus, Object resMsg,
			boolean nullable, String... includeProperties) {
		return ergodicListObject(objList, resStatus, resMsg,
				(propertyName, propertyValue) -> (ArrayUtils.contains(includeProperties, propertyName)
						&& (nullable ? true : (propertyValue != null))));
	}

	/**
	 * 返回一个ExtendMap
	 *
	 * @param resMsg
	 * @return
	 */
	public static ExtendMap statusOfMsg(Integer status, String resMsg) {
		ExtendMap simpleMap = getExtendMap(4);
		initCommon(simpleMap, status, resMsg);
		return simpleMap;
	}

	/**
	 * 简便的返回SUCCESS ,带信息
	 *
	 * @param resMsg
	 * @return
	 */
	public static ExtendMap successOfMsg(Object resMsg) {
		ExtendMap simpleMap = getExtendMap(4);
		initCommon(simpleMap, SUCCESS, resMsg);
		return simpleMap;
	}

	/**
	 * 简便的返回error ,带信息
	 *
	 * @param resMsg
	 * @return
	 */
	public static ExtendMap errorOfMsg(Object resMsg) {
		ExtendMap simpleMap = getExtendMap(4);
		initCommon(simpleMap, ERROR, resMsg);
		return simpleMap;
	}
	
	/**
	 * 简便的返回error ,带信息
	 *
	 * @param resMsg
	 * @param resStatus
	 * @return
	 */
	public static ExtendMap errorOfMsg(int resStatus, Object resMsg) {
		ExtendMap simpleMap = getExtendMap(4);
		initCommon(simpleMap, resStatus, resMsg);
		return simpleMap;
	}

	/**
	 * 简便的返回SUCCESS
	 *
	 * @return
	 */
	private static HashMap<String, Object> createSimpleMap(boolean isSUccess) {
		return isSUccess ? successOfMsg(null) : errorOfMsg(null);
	}

	/**
	 * 处理条件的元组
	 */
	private static class ProcessCondition<C,D,E>{
		private String keyName;
		private Object value;
		private Class<E> eClass;
		private Function<C,D> function;



		public ProcessCondition(String keyName, Object value, Class<E> eClass, Function<C, D> function) {
			this.keyName = keyName;
			this.value = value;
			this.eClass = eClass;
			this.function = function;
		}

		public boolean isMatch(String k, Object v, Class t){
			//校验名称是否符合
			if (keyName!=null) {
				if (!StringUtils.equals(keyName,k)) {
					return false;
				}
			}
			//校验类型是否符合
			if (eClass!=null) {
				if (!eClass.equals(t)) {
					return false;
				}
			}
			//检验value是否符合
			if (value!=null) {
				if (!value.equals(v)) {
					return false;
				}
			}
			return true;
		}

		public boolean isEmpty(){
			return keyName==null&&value==null&&eClass==null;
		}

	}



	/**
	 * 扩展map类
	 */
	public static class ExtendMap extends TransferAbleMap{

		private static final String DESC = "Desc";



		public ExtendMap(int initialCapacity) {
			super(initialCapacity);
		}

		/**
		 * 添加额外的参数
		 *
		 * @param paramterName
		 * @param paramterValue
		 * @return
		 */
		public ExtendMap addResParameter(String paramterName, Object paramterValue) {
			put(paramterName, paramterValue);
			return this;
		}

		/**
		 * 删除额外的参数
		 *
		 * @param paramterName
		 * @return
		 */
		public ExtendMap removeResParameter(String paramterName) {
			this.remove(paramterName);
			return this;
		}

		/**
		 * 更改参数名称-针对不是列表的map
		 *
		 * @param orginKeyName
		 * @param newKeyName
		 * @return
		 */
		public ExtendMap reNameParameter(String orginKeyName, String newKeyName) {
			return (ExtendMap) reNameParameterInMap(this, orginKeyName, newKeyName);
		}

		/**
		 * 批量更新名称-针对不是列表的map
		 * 
		 * @param oldKeyToNewKeyMapRel
		 * @return
		 */
		public ExtendMap reNameParameter(Map<String, String> oldKeyToNewKeyMapRel) {
			if (!MapUtils.isEmpty(oldKeyToNewKeyMapRel)) {
				oldKeyToNewKeyMapRel.forEach((orginKey, newKey) -> {
					reNameParameter(orginKey, newKey);
				});
			}
			return this;
		}

		/**
		 * 更改参数名称-针对列表的map
		 * 
		 * @param orginKeyName
		 * @param newKeyName
		 * @return
		 */
		public ExtendMap reNameParameterInList(String orginKeyName, String newKeyName) {

			if (this == null) {
				throw new IllegalStateException("源对象为null");
			}

			List dataList = (List) this.get(DATA_LIST);
			if (CollectionUtils.isEmpty(dataList)) {
				throw new IllegalStateException("列表数据为空");
			}
			dataList.forEach(
					mapInList -> reNameParameterInMap((Map<String, Object>) mapInList, orginKeyName, newKeyName));
			return this;
		}

		/**
		 * 批量更新名称-针对是列表的map
		 * 
		 * @param oldKeyToNewKeyMapRel
		 * @return
		 */
		public ExtendMap reNameParameterInList(Map<String, String> oldKeyToNewKeyMapRel) {
			if (!MapUtils.isEmpty(oldKeyToNewKeyMapRel)) {
				oldKeyToNewKeyMapRel.forEach((orginKey, newKey) -> reNameParameterInList(orginKey, newKey));
			}
			return this;
		}

		/**
		 * 为指定key增加描述
		 * 
		 * @param key
		 * @param getKeyDesc
		 * @return
		 */
		public ExtendMap addParameterDesc(String key, Function<Object, Object> getKeyDesc) {
			String addKey = key + DESC;
			addResParameter(addKey, getKeyDesc.apply(this.get(key)));
			return this;
		}

		/**
		 * 为列表中的指定key增加描述
		 * 
		 * @param key
		 * @param getKeyDesc
		 * @return
		 */
		public ExtendMap addParameterDescInList(String key, Function<Object, Object> getKeyDesc) {
			List<Map> dataList = (List) get(DATA_LIST);
			if (!CollectionUtils.isEmpty(dataList)) {
				dataList.stream().forEach(e -> {
					String addKey = key + DESC;
					e.put(addKey, getKeyDesc.apply(e.get(key)));
				});
			}
			return this;
		}

		/**
		 * 为列表中的map(Object对应的beanMap)进行加工
		 * 
		 * @param processMap
		 * @return
		 */
		public ExtendMap processObjInList(Consumer<Map<String, Object>> processMap) {
			List<Map> dataList = (List) get(DATA_LIST);
			if (!CollectionUtils.isEmpty(dataList)) {
				dataList.stream().forEach(e -> processMap.accept(e));
			}
			return this;
		}

		/**
		 * 重命名指定Map中的参数
		 * 
		 * @param unChangedMap
		 * @param orginKeyName
		 * @param newKeyName
		 * @return
		 */
		private Map<String, Object> reNameParameterInMap(Map<String, Object> unChangedMap, String orginKeyName,
				String newKeyName) {
			if (unChangedMap == null) {
				throw new IllegalStateException("源对象为null");
			}
			if (unChangedMap.containsKey(orginKeyName)) {
				unChangedMap.put(newKeyName, unChangedMap.get(orginKeyName));
				unChangedMap.remove(orginKeyName);
			}
			return unChangedMap;
		}



	}

	/**
	 * 初始化公共属性
	 *
	 * @param resultMap
	 * @param resStatus
	 * @param resMsg
	 */
	private static void initCommon(Map<String, Object> resultMap, Integer resStatus, Object resMsg) {
		Assert.notNull(resStatus, "返回状态码不能为null！");
		resultMap.put("resStatus",  String.valueOf(resStatus));
		resultMap.put("resMsg", resMsg);
	}

	/**
	 * 填充Beanmap
	 *
	 * @param source
	 *            源对象
	 * @param resultMap
	 *            结果Map
	 * @param decideFunction
	 *            判断是否可以添加的函数
	 * @param <T>
	 */
	private static <T> void fullBeanMap(T source, Map<String, Object> resultMap,
			BiFunction<String, Object, Boolean> decideFunction) {
		if (source != null) {
			if (source instanceof Map) {
//				// 如果是map不需要做转换
                resultMap.putAll((Map<? extends String, ?>) source);
                //过滤map中key值
//                putMap(source, resultMap, decideFunction);
				return ;
			}
			resultMap.putAll(BeanPropertiesUtil.getBeanMap(source,decideFunction ,true));
		}
	}

	/**
	 * 带有转换性质的map
	 */
	public static class TransferAbleMap extends HashMap<String, Object> {

		private List<ProcessCondition> processConditions=new ArrayList<>();

		public TransferAbleMap(int size){
			super(size);
		}
		//初始化一个处理BigDeciaml的处理类
		{
			processConditions.add(new ProcessCondition(null, null, BigDecimal.class, (value) ->
					((BigDecimal) value).setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()));
		}

		public void setProcessConditions(List<ProcessCondition> processConditions) {

			if (!CollectionUtils.isEmpty(processConditions)) {
				this.processConditions.addAll(processConditions);
			}
		}

		/**
		 *  @Description    ：重写put方法,添加一些处理和校验
		 *  @Method_Name    ：put
		 *  @param key
		 *  @param value
		 *  @return java.lang.Object
		 *  @Creation Date  ：2018/5/14
		 *  @Author         ：zhongpingtang@hongkun.com.cn
		 */
		@Override
		public Object put(String key, Object value) {
			final Object[] newVlaue = {null};
			if (!CollectionUtils.isEmpty(processConditions)) {
				processConditions.stream().forEach(processCondition -> {
					if (processCondition.isMatch(key,value,value==null?null:value.getClass())) {
						if (processCondition.function !=null) {
							newVlaue[0] =  processCondition.function.apply(value);
						}
					}
				});
			}
			return super.put(key, newVlaue[0]==null?value:newVlaue[0]);
		}

		@Override
		public void putAll(Map<? extends String, ?> m) {
			//转化为本类的添加
			if (!CollectionUtils.isEmpty(m)) {
				m.forEach((key,value)-> put(key, value));
			}
		}
	}


	/**
	 * 遍历list中的属性
	 *
	 * @param objList
	 * @param resStatus
	 * @param resMsg
	 * @param decideFunction
	 * @param <T>
	 * @return
	 */
	private static <T> ExtendMap ergodicListObject(List<T> objList, Integer resStatus, Object resMsg,
			BiFunction<String, Object, Boolean> decideFunction) {
		List<Map<String, Object>> dataList = new ArrayList<>(16);
		ExtendMap resultMap = getExtendMap(16);
		initCommon(resultMap, resStatus, resMsg);
		if (!CollectionUtils.isEmpty(objList)) {
			objList.stream().forEach(sourceBean -> {
				TransferAbleMap tempBeanMap = new TransferAbleMap(16);
				//传递处理函数
				tempBeanMap.setProcessConditions(processConditionThreadLocal.get());
				processConditionThreadLocal.remove();
				if (sourceBean instanceof Map) {
					tempBeanMap.putAll((Map<? extends String, ?>) sourceBean);
                    //过滤map中key值
//                    putMap(sourceBean, tempBeanMap, decideFunction);
				} else {
					fullBeanMap(sourceBean, tempBeanMap, decideFunction);
				}

				if (!MapUtils.isEmpty(tempBeanMap)) {
					dataList.add(tempBeanMap);
				}
			});
		}
		resultMap.put(DATA_LIST, dataList);
		return resultMap;
	}

	/**
	*  过滤map key值
	*  @param mapSource
	*  @param resultMap
	*  @param decideFunction
	*  @return void
	*  @date                    ：2018/10/23
	*  @author                  ：zc.ding@foxmail.com
	*/
	private static <T> void putMap(T mapSource, Map<String, Object> resultMap, BiFunction<String, Object, Boolean> decideFunction){
        Map<? extends String, ?> tmp = (Map<? extends String, ?>) mapSource;
        tmp.forEach((k, v) -> {
            if(decideFunction.apply(k, v)){
                resultMap.put(k, v);
            }
        });
    }
}
