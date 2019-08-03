package com.yirun.framework.redis;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import com.yirun.framework.core.exception.JedisFrameworkExpception;
import com.yirun.framework.core.utils.PropertiesHolder;

/**
 * 
 * @Description   : redis连接池（单例）
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.RedisSinglePool.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public class RedisSinglePool {
	private final Logger logger = Logger.getLogger(RedisSinglePool.class);
	
	/**定义一个静态私有变量(使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用) */
	private static volatile RedisSinglePool instance ; 
	private  JedisPool pool = null;
//	private  Jedis jedis = null;
	/**
	  *  @Description    : 私有的构造函数，初始化jedis pool 
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年2月28日
	 */
	private RedisSinglePool() {
    	if(pool == null){
    		pool = initPoolParams();
    	}else{
    		pool = getPool();	
    	}
    }
	/**
	 * 
	  *  @Description    : 暴露方法，返回当前类RedisSinglePool
	  *  @Method_Name    : getInstance
	  *  @return         : RedisSinglePool   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年2月28日
	 */
	public static RedisSinglePool getInstance() {
		if(instance==null){
			//同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
			synchronized(RedisSinglePool.class){
				 if (instance == null) {
	                 instance = new RedisSinglePool();   
	             }   
			}
		}
		return instance;
	}
	
	
	private  JedisPool initPoolParams(){
		JedisPool jedisPool = null;
		JedisPoolConfig poolconfig = new JedisPoolConfig();
		poolconfig.setMaxIdle(Integer.parseInt(PropertiesHolder.getProperty("redis.pool.maxIdle")));
        poolconfig.setMaxTotal(Integer.parseInt(PropertiesHolder.getProperty("redis.pool.maxTotal")));
    	poolconfig.setMaxWaitMillis(Integer.parseInt(PropertiesHolder.getProperty("redis.pool.maxWait")));
    	poolconfig.setTestOnBorrow(Boolean.parseBoolean((PropertiesHolder.getProperty("redis.pool.testOnBorrow"))));
    	poolconfig.setTestOnReturn(Boolean.parseBoolean(PropertiesHolder.getProperty("redis.pool.testOnReturn")));
//通过Properties读取配置文件，出现某些情况无法读取问题，废弃不用		
//		Properties prop = getPropByClassPath();
//        poolconfig.setMaxIdle(Integer.parseInt(prop.getProperty("redis.pool.maxIdle")));
//        poolconfig.setMaxTotal(Integer.parseInt(prop.getProperty("redis.pool.maxTotal")));
//    	poolconfig.setMaxWaitMillis(Integer.parseInt(prop.getProperty("redis.pool.maxWait")));
//    	poolconfig.setTestOnBorrow(Boolean.parseBoolean((prop.getProperty("redis.pool.testOnBorrow"))));
//    	poolconfig.setTestOnReturn(Boolean.parseBoolean(prop.getProperty("redis.pool.testOnReturn")));
    	String password = PropertiesHolder.getProperty("redis.password");
    	if(StringUtils.isBlank(password)){
			String ip = PropertiesHolder.getProperty("redis.ip");
			logger.info("Redis-Current-IP:"+ip);
    		jedisPool = new JedisPool(poolconfig,ip,Integer.parseInt(PropertiesHolder.getProperty("redis.port")),100000);
    	}else{
        	jedisPool = new JedisPool(poolconfig, PropertiesHolder.getProperty("redis.ip"), 
        			Integer.parseInt(PropertiesHolder.getProperty("redis.port")), 100000,
        			password, false);
    	}
        return jedisPool;
	} 
//通过Properties读取配置文件，出现某些情况无法读取问题，废弃不用	
//	private static Properties getPropByClassPath() {
//		Properties prop = null;
//		try {
//		    String filePath = RedisSinglePool.class.getResource("/application-redis.properties").getPath();
//			InputStream is = new BufferedInputStream (new FileInputStream(filePath));
//			prop = new Properties();
//			prop.load(is);
//		} catch (Exception e) {
//			throw new RuntimeException("can't find application-redis.properties file.");
//		}
//		return prop;
//	}
	
	/**
	  *  @Description    : 公共方法，返回jedisPool对象
	  *  @Method_Name    : getPool
	  *  @return         : JedisPool   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年2月28日
	 */
	public  JedisPool getPool(){
		if(pool == null){
	   		pool = initPoolParams();
	   	}
		return pool;
	}
	/**
	 * 
	  *  @Description    : 获取jedis连接
	  *  @Method_Name    : getJedis
	  *  @return         : Jedis   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年2月28日
	 */
	public synchronized Jedis getJedis(){
		if(pool == null){
    		pool = getPool();
    	}
		Jedis jedis =   null;
		try {
			jedis =  pool.getResource();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return jedis;
	}
	/**
	  *  @Description    : 释放连接到连接池,returnResource、returnBrokenResource官方已经废弃,重写了close方法作为替代
	  *  @Method_Name    : returnResource
	  *  @param @param pool
	  *  @param @param redis
	  *  @return         : boolean   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年2月28日
	 */
//    public boolean returnResource(JedisPool pool, Jedis redis) {
//		if (pool!=null&&redis != null) {
//			try{
//				pool.returnResource(redis);
//	        }catch(Exception e){
//	            pool.returnBrokenResource(redis);
//	            e.printStackTrace();
//	        }
//			return true;
//		}else{
//			logger.info("RedisSinglePool.returnResource=====redis:null");
//			return false;
//		}
//	}
    
	
//    public boolean returnResource(Jedis redis){
//    	return returnResource(pool,redis);
//    }
    /**
     * 
      *  @Description    : 返回jedis链接到连接池
      *  @Method_Name    : close
      *  @param @param jedis
      *  @return         : void   
      *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
      *  @date			  ：2017年3月15日
     */
	public void close(Jedis jedis){
		try {
			if(jedis!=null){
				jedis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 
      *  @Description    : 可用连接数
      *  @Method_Name    : userConnections
      *  @param @return
      *  @return         : int   
      *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
      *  @date			  ：2017年3月15日
     */
    public  int userConnections(){
    	return pool.getNumActive();
    }
    /**
     * 
      *  @Description    : 空闲连接数
      *  @Method_Name    : getNumIdle
      *  @param @return
      *  @return         : int   
      *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
      *  @date			  ：2017年3月15日
     */
    public int getNumIdle(){
    	return pool.getNumIdle();
    }
    
    
    public  void setString(String key ,String value){  
    	Jedis jedis = getJedis();
    	System.out.println("jedis==============================="+jedis.toString());
        try {  
        	jedis.set(key,value);  
        } catch (Exception e) {  
            logger.error("Set key error : "+e);  
        }finally{
        	jedis.close();
        }
    }  
       
    public String getValueByKey(String key) throws JedisFrameworkExpception {
		String result = null;
		Jedis jedis = getJedis();
		try {
			result = jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = JedisUtils.BASIC_ERROR_MSG 
					+ "method:getValueByKey[String]"+",params key:"+key+","
					+JedisUtils.JEDIS_SERVICE_ERROR_URL;
			throw new JedisFrameworkExpception(errorMsg);
		}finally{
			jedis.close();
		}
		return result;
	}
}
