package com.yirun.framework.redis;

import com.yirun.framework.core.exception.JedisFrameworkExpception;
import com.yirun.framework.core.utils.PropertiesHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @Description   : redis连接池（单例）
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.RedisSinglePool.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public class RedisSinglePoolCluster {
	private final Logger logger = Logger.getLogger(RedisSinglePoolCluster.class);

	/**定义一个静态私有变量(使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用) */
	private static volatile RedisSinglePoolCluster instance ;
	private JedisCluster cluster = null;
	/**
	  *  @Description    : 私有的构造函数，初始化JedisCluster
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年2月28日
	 */
	private RedisSinglePoolCluster() {
    	if(cluster == null){
			cluster = initPoolParams();
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
	public static RedisSinglePoolCluster getInstance() {
		if(instance==null){
			//同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
			synchronized(RedisSinglePoolCluster.class){
				 if (instance == null) {
	                 instance = new RedisSinglePoolCluster();
	             }
			}
		}
		return instance;
	}


	private  JedisCluster initPoolParams(){
		JedisPoolConfig poolconfig = new JedisPoolConfig();
		poolconfig.setMaxIdle(Integer.parseInt(PropertiesHolder.getProperty("redis.pool.maxIdle")));
        poolconfig.setMaxTotal(Integer.parseInt(PropertiesHolder.getProperty("redis.pool.maxTotal")));
    	poolconfig.setMaxWaitMillis(Integer.parseInt(PropertiesHolder.getProperty("redis.pool.maxWait")));
    	poolconfig.setTestOnBorrow(Boolean.parseBoolean((PropertiesHolder.getProperty("redis.pool.testOnBorrow"))));
    	poolconfig.setTestOnReturn(Boolean.parseBoolean(PropertiesHolder.getProperty("redis.pool.testOnReturn")));

    	String password = PropertiesHolder.getProperty("redis.password");
    	Integer timeout = Integer.parseInt(PropertiesHolder.getProperty("redis.timeout"));
		String ips = PropertiesHolder.getProperty("redis.cluster.ip");
		Set<HostAndPort> nodes = initRedisClusterIpAndPort(ips);

		if (StringUtils.isNotBlank(password)){
			return new JedisCluster(nodes,timeout,timeout,5,password,poolconfig);
		}
		return new JedisCluster(nodes, poolconfig);
	}

	/**
	*  @Description    ：获取jedis连接
	*  @Method_Name    ：getJedisCluster
	*
	*  @return redis.clients.jedis.JedisCluster
	*  @Creation Date  ：2018/6/12
	*  @Author         ：xuhui.liu@hongkun.com.cn 刘旭辉
	*/
	public synchronized JedisCluster getJedisCluster(){
		if(cluster == null){
			cluster = initPoolParams();
		}
		return cluster;
	}

	private Set<HostAndPort> initRedisClusterIpAndPort(String ips){
		Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
		String[] allIps = ips.split(",");
		for (int i=0;i<allIps.length;i++){
			String ipAndPort = allIps[i];
			String[] item = ipAndPort.split(":");
			String ip = item[0];
			Integer port = Integer.parseInt(item[1]);
			nodes.add(new HostAndPort(ip, port));
		}
		return nodes;
	}
}
