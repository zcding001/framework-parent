package com.yirun.framework.redis;

import com.yirun.framework.core.exception.JedisFrameworkExpception;
import com.yirun.framework.core.utils.JavaMapUtils;
import com.yirun.framework.core.utils.ObjectUtilsExtend;
import com.yirun.framework.core.utils.json.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * @Description : jedis工具类，为业务层提供jedis基础服务
 * @Project : framework-redis
 * @Program Name : com.yirun.framework.redis.JedisUtils.java
 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public class JedisUtils {
	/** 设置成功标识符 */
	public static final String SET_SUCCESS = "ok";
	/** 不设置超时时间 */
	public static final int NOT_EXPIRE = 0;

	public static final String JEDIS_SERVICE_ERROR_URL = "class:com.yirun.framework.redis.JedisUtils";
	public static final String BASIC_ERROR_MSG = "error:=====";

	private static final Logger logger = LoggerFactory.getLogger(JedisUtils.class);
	private static String errorMsg = "";
	// private static Jedis jedis = null;

	/**
	 * @Description : 根据key获取value
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年2月28日
	 */
	public static String get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String result = null;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			result = jedis.get(key);
		} catch (Exception e) {
			logger.error("get[String], key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("get(String key) error");
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : 根据key获取value
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月10日
	 */
	public static byte[] get(byte[] key) {
		if (key == null) {
			return null;
		}
		byte[] result = null;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			result = jedis.get(key);
		} catch (Exception e) {
			logger.error("get[byte], key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("get(byte key) error");
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * 
	 * @Description : 根据传入的数据类型，返回相应的泛型结果
	 * @Method_Name : getObjectForJson
	 * @param @param
	 *            key
	 * @param @param
	 *            clasz
	 * @return : T
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月10日
	 */
	public static <T> T getObjectForJson(String key, Class<T> clasz) {
		if (StringUtils.isBlank(key) || clasz == null) {
			return null;
		}
		T result = null;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			String jsonString = jedis.get(key);
			result = JsonUtils.json2Object(jsonString, clasz, null);
//			result = JsonUtils.json2GenericObject(jsonString, new TypeReference<T>(){}, null);
		} catch (Exception e) {
			logger.error("getObjectForJson[String,Class<T>], key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("getObjectForJson[String,Class<T>] error");
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}
	
	/**
	 * @Description : 从redis中获取json字符串，转化为各种类型
	 * @Method_Name : getObjectForJson
	 * @param key
	 * @param typeReference
	 *            new TypeReference< List<FamousUser> >(){}
	 * @return
	 * @return : T
	 * @Creation Date : 2017年7月20日 下午3:42:19
	 * @Author : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	public static <T> T getObjectForJson(String key, TypeReference<T> typeReference) {
		if (StringUtils.isBlank(key) || typeReference == null) {
			return null;
		}
		T result = null;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			String jsonString = jedis.get(key);
			result = JsonUtils.json2GenericObject(jsonString, typeReference, "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			logger.error("getObjectForJson[String,TypeReference<T>], key: {},typeReference: {}, 异常信息:\n",key,e);
			throw new JedisFrameworkExpception("getObjectForJson[String,TypeReference<T>] error");
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : redis 存储String 其他数据类型可以转化为jsonStr存储
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月1日
	 */
	public static boolean set(String key, String value) {
		if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
			return false;
		}
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			if (SET_SUCCESS.equals(jedis.set(key, value))) {
				result = true;
			}
		} catch (Exception e) {
			logger.error("set[String,String], key: {}, value: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("getObjectForJson[String,TypeReference<T>] error");
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : redis 存储byte[] 其他数据类型可以转化为byte[]存储
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月10日
	 */
	public static boolean set(byte[] key, byte[] value) {
		if (key == null || value == null) {
			return false;
		}
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			if (JedisUtils.SET_SUCCESS.equals(jedis.set(key, value))) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:set[byte[]&byte[]]" + ",params key:" + key.toString()
					+ ",value:" + value.toString() + "," + JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : 清空key对应的存储
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月1日
	 */
	public static void clear(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			jedis.set(key, "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 通过key删除键值对
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月1日
	 */
	public static void delete(String key) {
		if (!exists(key)) {
			return;
		}
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	/**
	 * 
	 *  @Description    : 通过key删除键值对 
	 *  @Method_Name    : delete
	 *  @param key
	 *  @return         : void
	 *  @Creation Date  : 2017年11月2日 上午9:31:10 
	 *  @Author         : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	public static void delete(byte[] key) {
		if (!exists(key)) {
			return;
		}
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	/**
	 * @Description : 存储redis键值对，并设置超时时间
	 * @Method_Name : set
	 * @param @param
	 *            key
	 * @param @param
	 *            value
	 * @param @param
	 *            seconds 超时时间：秒 如果传值<=0，不设置超时时间
	 * @return : boolean 成功：true
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月17日
	 */
	public static boolean set(byte[] key, byte[] value, int seconds) {
		if (key == null || value == null) {
			return false;
		}
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			if (seconds <= 0) {
				result = SET_SUCCESS.equals(jedis.set(key, value));
			} else {
				result = SET_SUCCESS.equals(jedis.setex(key, seconds, value));
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:set[byte[]&byte[]&int]" + ",params key:" + key + ",value:"
					+ value + ",seconds:" + seconds + "," + JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * 
	 * @Description : 存储redis键值对，并设置超时时间
	 * @Method_Name : set
	 * @param @param
	 *            key
	 * @param @param
	 *            value
	 * @param @param
	 *            seconds 超时时间：秒 如果传值<=0，不设置超时时间
	 * @return : boolean 成功：true
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月10日
	 */
	public static boolean set(String key, String value, int seconds) {
		if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
			return false;
		}
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			if (seconds <= 0) {
				result = SET_SUCCESS.equals(jedis.set(key, value));
			} else {
				result = SET_SUCCESS.equals(jedis.setex(key, seconds, value));
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:set[String&String&int]" + ",params key:" + key + ",value:"
					+ value + ",seconds:" + seconds + "," + JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : 对象转化为json字符串存储
	 * @Method_Name : setAsJson
	 * @param @param
	 *            key
	 * @param @param
	 *            value
	 * @param @param
	 *            seconds
	 * @return : boolean
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月10日
	 */
	public static boolean setAsJson(String key, Object value, int seconds) {
		if (StringUtils.isBlank(key) || value == null) {
			return false;
		}
		String jsonStr = "";
		boolean result = false;
		Jedis jedis = null;
		try {
			jsonStr = JsonUtils.toJson(value);
			jedis = RedisSinglePool.getInstance().getJedis();
			if (seconds > NOT_EXPIRE) {
				result = SET_SUCCESS.equals(jedis.setex(key, seconds, jsonStr));
			} else {
				result = SET_SUCCESS.equals(jedis.set(key, jsonStr));
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:setAsJson[String&Object&int]" + ",params key:" + key
					+ ",seconds:" + seconds + "," + JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : 对象转化为json字符串存储,不设置超时时间
	 * @Method_Name : setAsJson
	 * @param @param
	 *            key
	 * @param @param
	 *            value
	 * @param @return
	 * @return : boolean
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年6月20日
	 */
	public static boolean setAsJson(String key, Object value) {
		if (StringUtils.isBlank(key) || value == null) {
			return false;
		}
		return setAsJson(key, value, NOT_EXPIRE);
	}

	/**
	 * @Description : 存储对象（以hash方式存储）
	 * @Method_Name : setAsMap
	 * @param @param
	 *            key
	 * @param @param
	 *            value
	 * @param @param
	 *            seconds
	 * @return : boolean
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月20日
	 */
	public static boolean setAsMap(String key, Object value, int seconds) {
		if (StringUtils.isBlank(key) || value == null) {
			return false;
		}
		Map<String, String> object = new HashMap<String, String>();
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			object = JavaMapUtils.objectToMap(value);
			result = setMap(key, object);
			if (seconds > 0) {
				jedis.expire(key, seconds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:setAsMap[String&Object&int]" + ",params key:" + key
					+ ",seconds:" + seconds + "," + JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		}
		return result;
	}
	/**
	 * 
	 *  @Description    : 通过设置泛型获取object对象（redis中对象以map存储）
	 *  @Method_Name    : getObjectForMap
	 *  @param key
	 *  @param clasz
	 *  @return
	 *  @return         : T
	 *  @Creation Date  : 2017年11月8日 上午9:53:23 
	 *  @Author         : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObjectForMap(String key, Class<?> clasz) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		T t = null;
		try {
			map = getMap(key);
			t = (T) JavaMapUtils.mapToObject(map, clasz);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:getObjectForMap[key" + ",params key:" + key + ","
					+ JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		}
		return t;
	}

	/**
	 * 
	 * @Description : 缓存中是否存在key
	 * @Method_Name : exists
	 * @param @param
	 *            key
	 * @return : boolean 存在：true
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月10日
	 */
	public static boolean exists(String key) {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			result = jedis.exists(key);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:exists[String]" + ",params key:" + key + ","
					+ JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * 
	 * @Description : 缓存中是否存在key
	 * @Method_Name : exists
	 * @param @param
	 *            key
	 * @return : boolean 存在：true
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月10日
	 */
	public static boolean exists(byte[] key) {
		if (key == null) {
			return false;
		}
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			result = jedis.exists(key);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:exists[byte[]]" + ",params key:" + key + ","
					+ JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : 向缓存中添加元素field<—>value
	 * @Method_Name : setHash
	 * @param @param
	 *            key jedis键
	 * @param @param
	 *            field 存储的hash键
	 * @param @param
	 *            value 存储的hash值
	 * @return : boolean
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月17日
	 */
	public static boolean setHash(String key, String field, String value) {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			result = jedis.hset(key, field, value) > -1;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:setHash[String]" + ",params key:" + key + "," + JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		}
		return result;
	}

	/**
	 * @Description : 返回名称为key的hash中field对应的value
	 * @Method_Name : getHash
	 * @param @param
	 *            key
	 * @param @param
	 *            field
	 * @return : String
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月20日
	 */
	public static String getHash(String key, String field) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String result = null;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			result = jedis.hget(key, field);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:getHash[String]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		}
		return result;
	}

	/**
	 * @Description : hash存储---Map对象
	 * @Method_Name : setMap
	 * @param @param
	 *            key
	 * @param @param
	 *            map
	 * @return : boolean
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月20日
	 */
	public static boolean setMap(String key, Map<String, String> map) {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			result = SET_SUCCESS.equals(jedis.hmset(key, map));
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:setMap[String,Map]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : 通过key获取存储的Map对象
	 * @Method_Name : getMap
	 * @param @param
	 *            key
	 * @return : Map<String,String>
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年3月20日
	 */
	public static Map<String, String> getMap(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Map<String, String> result = null;
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			result = jedis.hgetAll(key);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:getMap[String]" + ",params key:" + key + "," + JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	public static boolean setList(String key, List<String> list) {
		boolean result = false;
		if (list == null || list.size() <= 0) {
			return result;
		}
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			for (String s : list) {
				jedis.lpush(key, s);
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:setList[String&list]" + ",params key:" + key + "," + JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return result;
	}

	/**
	 * @Description : 获取list<String>，如果获取list全部值，start，end分别复制为0，-1
	 * @Method_Name : getList
	 * @param @param
	 *            key
	 * @param @param
	 *            start
	 * @param @param
	 *            end
	 * @return : List<String>
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年5月23日
	 */
	public static List<String> getList(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			return jedis.lrange(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:getList[String&int&int]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 设置超时时间
	 * @Method_Name : setExpireTime
	 * @param @param
	 *            key
	 * @param @param
	 *            second 秒
	 * @return : boolean
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年5月23日
	 */
	public static boolean setExpireTime(String key, int second) {
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			long result = jedis.expire(key.getBytes(), second);
			return result > 0 ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:setExpireTime[string&int]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 通过byte形式存储object 对象 
	 * 此方法不推荐使用
	 * 推荐使用方法：
	 * 获取--getValueByKey(String,Class)
	 * 设置--setAsJson（String key，Object value）
	 * 	   setAsJson(String key, Object value, int seconds)
	 * @Method_Name : setAsByte
	 * @param @param
	 *            key
	 * @param @param
	 *            obj
	 * @return : boolean
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年5月24日
	 */
	@Deprecated
	public static boolean setAsByte(String key, Object obj) {
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			jedis.set(key.getBytes(), ObjectUtilsExtend.objectToBytes(obj));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:setAsByte[string&object]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 获取对象（通过byte形式存储的） 
	 * 此方法不推荐使用
	 * 推荐使用json进行存取：
	 * 获取--getValueByKey(String,Class)
	 * 设置--setAsJson（String key，Object value）
	 * 	   setAsJson(String key, Object value, int seconds)
	 * @Method_Name : getObject
	 * @param @param
	 *            key
	 * @return : T
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年5月24日
	 */
	@Deprecated
	public static <T> T getObjectForByte(String key) {
		Jedis jedis = null;
		byte[] buff = null;
		if (key == null) {
			return null;
		}
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			buff = jedis.get(key.getBytes());
			if (buff == null) {
				return null;
			}
			return ObjectUtilsExtend.bytesToObject(buff);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:getObjectForByte[string]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 剩余过期时间查询 单位：毫秒
	 * @Method_Name : getRemainTime
	 * @param @param
	 *            key
	 * @param @return
	 * @return : long
	 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
	 * @date ：2017年5月27日
	 */
	public static long getRemainTime(String key) {
		if (StringUtils.isBlank(key)) {
			return 0;
		}
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			long time = jedis.ttl(key.getBytes());
			return time;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:getRemainTime[string]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 将值放入队列
	 * @Method_Name : lpush
	 * @param key
	 * @param obj
	 * @return
	 * @return : boolean
	 * @Creation Date : 2017年7月11日 下午4:53:16
	 * @Author : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	public static boolean lpush(String key, Object obj) {
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			jedis.lpush(key.getBytes(), ObjectUtilsExtend.objectToBytes(obj));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:rpush[string]" + ",params key:" + key + "," + JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 拿出队列中最后一个元素(先进后出)
	 * @Method_Name : lpop
	 * @param key
	 * @return
	 * @return : T
	 * @Creation Date : 2017年7月11日 下午4:53:01
	 * @Author : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	public static <T> T rpop(String key) {
		Jedis jedis = null;
		byte[] buff = null;
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			buff = jedis.rpop(key.getBytes());
			if (buff == null) {
				return null;
			}
			return ObjectUtilsExtend.bytesToObject(buff);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:rpop[string]" + ",params key:" + key + "," + JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 取出元素（先进后出）
	 * @Method_Name : lpop
	 * @param key
	 * @return
	 * @return : T
	 * @Creation Date : 2017年7月11日 下午5:12:07
	 * @Author : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	public static <T> T lpop(String key) {
		Jedis jedis = null;
		byte[] buff = null;
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			buff = jedis.lpop(key.getBytes());
			if (buff == null) {
				return null;
			}
			return ObjectUtilsExtend.bytesToObject(buff);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:lpop[string]" + ",params key:" + key + "," + JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * @Description : 获取队列集合
	 * @Method_Name : pullList
	 * @param key
	 * @return
	 * @return : List<?>
	 * @Creation Date : 2017年7月11日 下午4:52:44
	 * @Author : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	public static List<?> pullList(String key) {
		Jedis jedis = null;
		List<byte[]> buff = null;
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			buff = jedis.lrange(key.getBytes(), 0, -1);
			if (buff == null) {
				return null;
			}
			List<?> ts = new ArrayList<>();
			for (byte[] b : buff) {
				ts.add(ObjectUtilsExtend.bytesToObject(b));
			}
			return ts;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:pullList[string]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	/**
	 *  @Description    : 正则检索JSON数据
	 *  @Method_Name    : getValuesByKey
	 *  @param pattern	正则规则
	 *  @param clazz	JSON对应的对象
	 *  @return         : List<T>
	 *  @Creation Date  : 2017年11月6日 下午4:56:13 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static <T> List<T> get(String pattern, Class<T> clazz) {
		List<T> result = new ArrayList<>();
		logger.info("检索记得KEY的正则为：" + pattern);
		if (StringUtils.isNotBlank(pattern)) {
			Jedis jedis = null;
			try {
				jedis = RedisSinglePool.getInstance().getJedis();
				Set<byte[]> set = jedis.keys(pattern.getBytes());
				if(set !=  null && set.size() > 0) {
					for(byte[] buf : set) {
						result.add(ObjectUtilsExtend.bytesToObject(jedis.get(buf)));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				errorMsg = JedisUtils.BASIC_ERROR_MSG + "method:getValuesByKey[String&clasz]" + ",params pattern:" + pattern
						+ ",clasz:" + clazz  + "," + JedisUtils.JEDIS_SERVICE_ERROR_URL;
				throw new JedisFrameworkExpception(errorMsg);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}
		return result;
	}

	/**
	 * 通过前缀模糊删除key，比如传入a,代表所以a开头的key都会被删除
	 * @param key
	 * @return
	 * @Author         : zhonpingtang
	 *
	 */
	public static boolean  deleteKeysByPrefix(String key){
		Jedis jedis = null;
		try {
			jedis = RedisSinglePool.getInstance().getJedis();
			key = key + "*";
			Set<byte[]> unDeleteKeys = jedis.keys(key.getBytes());
			if (!CollectionUtils.isEmpty(unDeleteKeys)) {
				unDeleteKeys.stream().forEach(jedis::del);
			}
            Set<String> set = jedis.keys(key);
            if (!CollectionUtils.isEmpty(set)) {
                unDeleteKeys.stream().forEach(jedis::del);
            }
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = BASIC_ERROR_MSG + "method:deleteKeysByPrefix[string]" + ",params key:" + key + ","
					+ JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

}
