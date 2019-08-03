package com.yirun.framework.core.utils.json;

import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.map.ser.std.NullSerializer;
import org.codehaus.jackson.type.TypeReference;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.yirun.framework.core.utils.DateUtils;

/**
 * @Description : JSON工具类
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.utils.json.JsonUtils.java
 * @Author : imzhousong@gmail.com 周松
 */
public class JsonUtils {

	private static final Logger log = Logger.getLogger(JsonUtils.class);

	private static final String INCLUDE_FILTER = "includeFilter";

	private static final String EXECUTE_FILTER = "executeFilter";
	private static XmlMapper xmlMapper = new XmlMapper();

	/**
	 * @Description : JSON串转换为Java泛型对象，可以是各种类型。
	 * @Method_Name : json2GenericObject
	 * @param jsonString
	 *            JSON字符串
	 * @param typeReference
	 *            new TypeReference< List<FamousUser> >(){}
	 * @param dateFormat
	 *            日期格式
	 * @return : T
	 * @Author : imzhousong@gmail.com 周松
	 */
	@SuppressWarnings("unchecked")
	public static <T> T json2GenericObject(String jsonString, TypeReference<T> typeReference, String dateFormat) {
		if (StringUtils.isNotEmpty(jsonString)) {
			try {
				return (T) initObjectMapper(dateFormat).readValue(jsonString, typeReference);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * @Description : JSON串转换为Java对象
	 * @Method_Name : json2Object
	 * @param jsonString
	 *            JSON字符串
	 * @param clazz
	 *            Java对象类
	 * @param dateFormat
	 *            日期格式
	 * @return : T
	 * @Author : imzhousong@gmail.com 周松
	 */
	public static <T> T json2Object(String jsonString, Class<T> clazz, String dateFormat) {
		if (StringUtils.isNotEmpty(jsonString)) {
			try {
				return (T) initObjectMapper(dateFormat).readValue(jsonString, clazz);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * @Description : Java对象转JSON字符串
	 * @Method_Name : toJson
	 * @param object
	 *            目标对象
	 * @param executeFields排除字段
	 * @param includeFields包含字段
	 * @param dateFormat
	 *            时间格式化
	 * @param isPretty
	 *            是否格式化打印 default false
	 * @return : String
	 * @Author : imzhousong@gmail.com 周松
	 */
	public static String toJson(Object object, String[] executeFields, String[] includeFields, String dateFormat,
			boolean isPretty) {
		String jsonString = "";
		try {
			JsonBeanSerializerFactory beanFactory = JsonBeanSerializerFactory.instance;

			ObjectMapper objectMapper = initObjectMapper(dateFormat);
			if (includeFields != null) {
				objectMapper.setFilters(new SimpleFilterProvider().addFilter(INCLUDE_FILTER,
						SimpleBeanPropertyFilter.filterOutAllExcept(includeFields)));
				beanFactory.setFilterId(INCLUDE_FILTER);
				objectMapper.setSerializerFactory(beanFactory);

			} else if (includeFields == null && executeFields != null) {
				objectMapper.setFilters(new SimpleFilterProvider().addFilter(EXECUTE_FILTER,
						SimpleBeanPropertyFilter.serializeAllExcept(executeFields)));
				beanFactory.setFilterId(EXECUTE_FILTER);
				objectMapper.setSerializerFactory(beanFactory);
			}
			if (isPretty) {
				jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
			} else {
				jsonString = objectMapper.writeValueAsString(object);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return jsonString;
	}

	/**
	 * @Description : 使用默认行为
	 * @Method_Name : toJson
	 * @param object
	 * @return : String
	 * @Author : imzhousong@gmail.com 周松
	 */
	public static String toJson(Object object) {
		return toJson(object, null, null, null, false);
	}

	/**
	 * @Description : 简易JSON拼装
	 * @Method_Name : toSimpleJson
	 * @param key
	 * @param value
	 * @return : String{"key":"value"}
	 * @Author : imzhousong@gmail.com 周松
	 */
	public static String toSimpleJson(String key, String value) {
		return toSimplesJson(key, value);
	}

	public static String toSimpleJson(String key, Integer value) {
		return toSimplesJson(key, value);
	}

	public static String toSimpleJson(String key, Long value) {
		return toSimplesJson(key, value);
	}

	private static String toSimplesJson(String key, Object value) {
		return "{\"" + key + "\":\"" + value + "\"}";
	}

	/**
	 * @Description : 自定义时间格式
	 * @Method_Name : toJson
	 * @param object
	 *            目标对象
	 * @param dateFormat
	 *            时间格式 DateUtils.DATE_**
	 * @return : String
	 * @Author : imzhousong@gmail.com 周松
	 */
	public static String toJson(Object object, String dateFormat) {
		return toJson(object, null, null, dateFormat, false);
	}

	private static ObjectMapper initObjectMapper(String dateFormat) {
		StdSerializerProvider ssp = new StdSerializerProvider();
		ssp.setNullValueSerializer(NullSerializer.instance);
		ObjectMapper objectMapper = new ObjectMapper(null, ssp, null);
		/**
		 * 设置日期序列化及反序列化格式
		 */
		if (StringUtils.isNotEmpty(dateFormat)) {
			objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
		} else {
			objectMapper.setDateFormat(new SimpleDateFormat(DateUtils.DATE_HH_MM_SS));
		}
		objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

		return objectMapper;
	}

	/**
	 * @Description : 将XML转换为JSON
	 * @Method_Name : xmlToJson;
	 * @param xmlString
	 *            xml字符串格式
	 * @return
	 * @return : String;
	 * @Creation Date : 2018年1月24日 上午10:08:26;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public static String xmlToJson(String xmlString) {
		com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		StringWriter sw = new StringWriter();
		try {
			JsonParser jp = xmlMapper.getFactory().createParser(xmlString);
			JsonGenerator jg = objectMapper.getFactory().createGenerator(sw);
			while (jp.nextToken() != null) {
				jg.copyCurrentEvent(jp);
			}
			jp.close();
			jg.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
}