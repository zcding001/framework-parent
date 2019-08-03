package com.yirun.framework.redis;

import com.yirun.framework.core.exception.JedisFrameworkExpception;
import com.yirun.framework.core.utils.ObjectUtilsExtend;
import com.yirun.framework.core.utils.json.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.*;

/**
 * @Description : jedis工具类，为业务层提供jedis基础服务
 * @Project : framework-redis
 * @Program Name : com.yirun.framework.redis.JedisUtils.java
 * @Author : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public class JedisClusterUtils {
	/** 设置成功标识符 */
	public static final String SET_SUCCESS = "ok";
	/** 不设置超时时间 */
	public static final int NOT_EXPIRE = 0;
	private static final Logger logger = LoggerFactory.getLogger(JedisClusterUtils.class);

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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			result = cluster.get(key);
		} catch (Exception e) {
			logger.error("get[String], key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("get(String) error");
		} finally {
//			closeCluster(cluster);
		}
		return result;
	}

	private static void closeCluster(JedisCluster cluster){
		if (cluster != null) {
			try {
				cluster.close();
			} catch (IOException e) {
				logger.error("closeCluster:, 异常信息:\n",e);
			}
		}
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			result = cluster.get(key);
		} catch (Exception e) {
			logger.error("get[byte], key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("get(byte) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			String jsonString = cluster.get(key);
			result = JsonUtils.json2Object(jsonString, clasz, null);
		} catch (Exception e) {
			logger.error("getObjectForJson[String,Class<T>], key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("getObjectForJson[String,Class<T>] error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			String jsonString = cluster.get(key);
			result = JsonUtils.json2GenericObject(jsonString, typeReference, "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			logger.error("getObjectForJson[String,TypeReference<T>], key: {},typeReference: {}, 异常信息:\n",key,e);
			throw new JedisFrameworkExpception("getObjectForJson[String,TypeReference<T>] error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			if (SET_SUCCESS.equals(cluster.set(key, value))) {
				result = true;
			}
		} catch (Exception e) {
			logger.error("set[String,String], key: {}, value: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("getObjectForJson[String,TypeReference<T>] error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			if (JedisClusterUtils.SET_SUCCESS.equals(cluster.set(key, value))) {
				result = true;
			}
		} catch (Exception e) {
			logger.error("set[byte[],byte[]], key: {}, value: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("set(byte[] key, byte[] value) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			cluster.set(key, "");
		} catch (Exception e) {
			logger.error("clear(String), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("clear(String) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			cluster.del(key);
		} catch (Exception e) {
			logger.error("delete(String), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("delete(String) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			cluster.del(key);
		} catch (Exception e) {
			logger.error("delete(byte[]), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("delete(byte[]) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			if (seconds <= 0) {
				result = SET_SUCCESS.equals(cluster.set(key, value));
			} else {
				result = SET_SUCCESS.equals(cluster.setex(key, seconds, value));
			}
		} catch (Exception e) {
			logger.error("set(byte[] key, byte[] value, int seconds), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("set(byte[] key, byte[] value, int seconds) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			if (seconds <= 0) {
				result = SET_SUCCESS.equals(cluster.set(key, value));
			} else {
				result = SET_SUCCESS.equals(cluster.setex(key, seconds, value));
			}
		} catch (Exception e) {
			logger.error("set(String key, String value, int seconds), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("set(String key, String value, int seconds) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			jsonStr = JsonUtils.toJson(value);
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			if (seconds > NOT_EXPIRE) {
				result = SET_SUCCESS.equals(cluster.setex(key, seconds, jsonStr));
			} else {
				result = SET_SUCCESS.equals(cluster.set(key, jsonStr));
			}
		} catch (Exception e) {
			logger.error("setAsJson(String, Object, int), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("setAsJson(String, Object, int) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			result = cluster.exists(key);
		} catch (Exception e) {
			logger.error("exists(String), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("exists(String) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			result = cluster.exists(key);
		} catch (Exception e) {
			logger.error("exists(byte[]), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("exists(byte[]) error");
		} finally {
//			closeCluster(cluster);
		}
		return result;
	}

	public static boolean setList(String key, List<String> list) {
		boolean result = false;
		if (list == null || list.size() <= 0) {
			return result;
		}
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			for (String s : list) {
				cluster.lpush(key, s);
			}
			result = true;
		} catch (Exception e) {
			logger.error("setList(String key, List<String> list), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("setList(String key, List<String> list) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			return cluster.lrange(key, start, end);
		} catch (Exception e) {
			logger.error("getList(String key, int start, int end), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("getList(String key, int start, int end) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			long result = cluster.expire(key.getBytes(), second);
			return result > 0 ? true : false;
		} catch (Exception e) {
			logger.error("setExpireTime(String key, int second), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("setExpireTime(String key, int second) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			long time = cluster.ttl(key.getBytes());
			return time;
		} catch (Exception e) {
			logger.error("getRemainTime(String key), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("getRemainTime(String key) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			cluster.lpush(key.getBytes(), ObjectUtilsExtend.objectToBytes(obj));
			return true;
		} catch (Exception e) {
			logger.error("lpush(String key, Object obj), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("lpush(String key, Object obj) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		byte[] buff = null;
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			buff = cluster.rpop(key.getBytes());
			if (buff == null) {
				return null;
			}
			return ObjectUtilsExtend.bytesToObject(buff);
		} catch (Exception e) {
			logger.error("rpop(String key), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("rpop(String key) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		byte[] buff = null;
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			buff = cluster.lpop(key.getBytes());
			if (buff == null) {
				return null;
			}
			return ObjectUtilsExtend.bytesToObject(buff);
		} catch (Exception e) {
			logger.error("lpop(String key), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("lpop(String key) error");
		} finally {
//			closeCluster(cluster);
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
		JedisCluster cluster = null;
		List<byte[]> buff = null;
		if (StringUtils.isBlank(key)) {
			return null;
		}
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			buff = cluster.lrange(key.getBytes(), 0, -1);
			if (buff == null) {
				return null;
			}
			List<?> ts = new ArrayList<>();
			for (byte[] b : buff) {
				ts.add(ObjectUtilsExtend.bytesToObject(b));
			}
			return ts;
		} catch (Exception e) {
			logger.error("pullList(String key), key: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("pullList(String key) error");
		} finally {
//			closeCluster(cluster);
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
			JedisCluster cluster = null;
			try {
				cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
//				Set<byte[]> set = cluster.hkeys(pattern.getBytes());
				Set<byte[]> set = initKeys(pattern,cluster);
				if(set !=  null && set.size() > 0) {
					for(byte[] buf : set) {
						result.add(ObjectUtilsExtend.bytesToObject(cluster.get(buf)));
					}
				}
			} catch (Exception e) {
				logger.error("get(String pattern, Class<T> clazz), pattern: {}, 异常信息:\n", pattern,e);
				throw new JedisFrameworkExpception("get(String pattern, Class<T> clazz) error");
			} finally {
//				closeCluster(cluster);
			}
		}
		return result;
	}

	private static Set<byte[]> initKeys(String pattern,JedisCluster cluster ){
		Set<byte[]> keys = new HashSet<>();
		Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
		for(String k : clusterNodes.keySet()){
			logger.debug("Getting keys from: {}", k);
			JedisPool jp = clusterNodes.get(k);
			Jedis connection = jp.getResource();
			try {
				Set<byte[]> sets = connection.keys(pattern.getBytes());
				keys.addAll(sets);
			} catch(Exception e){
				logger.error("Getting keys error: {}", e);
			} finally{
				logger.debug("Connection closed.");
				connection.close();//用完一定要close这个链接！！！
			}
		}
		return keys;
	}

	private static Set<String> initKeysForString(String pattern,JedisCluster cluster ){
		Set<String> keys = new HashSet<>();
		Map<String, JedisPool> clusterNodes = cluster.getClusterNodes();
		for(String k : clusterNodes.keySet()){
			logger.debug("Getting keys from: {}", k);
			JedisPool jp = clusterNodes.get(k);
			Jedis connection = jp.getResource();
			try {
				Set<String> sets = connection.keys(pattern);
				keys.addAll(sets);
			} catch(Exception e){
				logger.error("Getting keys error: {}", e);
			} finally{
				logger.debug("Connection closed.");
				connection.close();//用完一定要close这个链接！！！
			}
		}
		return keys;
	}
	/**
	 * 通过前缀模糊删除key，比如传入a,代表所以a开头的key都会被删除
	 * @param key
	 * @return
	 * @Author         : zhonpingtang
	 *
	 */
	public static boolean  deleteKeysByPrefix(String key){
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			key = key + "*";
			Set<byte[]> unDeleteKeys = initKeys(key,cluster);
			if (!CollectionUtils.isEmpty(unDeleteKeys)) {
				unDeleteKeys.stream().forEach(cluster::del);
			}
            Set<String> set = initKeysForString(key,cluster);
            if (!CollectionUtils.isEmpty(set)) {
                unDeleteKeys.stream().forEach(cluster::del);
            }
			return true;
		} catch (Exception e) {
			logger.error("deleteKeysByPrefix(String key), pattern: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("deleteKeysByPrefix(String key) error");
		} finally {
//			closeCluster(cluster);
		}
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			result = SET_SUCCESS.equals(cluster.hmset(key, map));
		} catch (Exception e) {
			logger.error("setMap(String key, Map<String, String> map), pattern: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("setMap(String key, Map<String, String> map) error");
		} finally {
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
		JedisCluster cluster = null;
		try {
			cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
			result = cluster.hgetAll(key);
		} catch (Exception e) {
			logger.error("getMap(String key), pattern: {}, 异常信息:\n", key,e);
			throw new JedisFrameworkExpception("getMap(String key) error");
		} finally {
		}
		return result;
	}
	
	public static JedisCluster getJedisCluster(){
        return RedisSinglePoolCluster.getInstance().getJedisCluster();
    }

}
