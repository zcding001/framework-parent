package com.yirun.framework.redis;

import com.yirun.framework.core.commons.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;

/**
 * @Description   : 分布式锁工具类
 * 使用方法：
 * JedisLockUtils jlu =  new JedisLockUtils();
 * jlu.lock(lockKey,expire);
 * 业务处理
 * jlu.freeLock(lockKey)
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.JedisLock.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public class JedisClusterLock {
	private final Logger logger = Logger.getLogger(JedisClusterLock.class);
	private JedisCluster cluster;

	public JedisClusterLock(){
		cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
	}
	
	/**
	 * 获取锁
	 * @param lockKey
	 * @return
	 */
	public boolean lock(String lockKey){
		return lock(lockKey, Constants.LOCK_EXPIRES, Constants.LOCK_WAITTIME);
	}
	/**
	  *  @Description    : 获取锁
	  *  @Method_Name    : lock
	  *  @param @param lockKey  锁名称
	  *  @param @param expire   超时时间:秒
	  *  @param @param waitTime 等待时间 :秒
	  *  @return         : boolean   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年3月1日
	 */
	public boolean lock(String lockKey, int expire,int waitTime) {
		if(expire<Constants.LOCK_EXPIRES_MIN||expire>Constants.LOCK_EXPIRES_MAX
				||waitTime<Constants.LOCK_WAITTIME_MIN||waitTime>Constants.LOCK_WAITTIME_MAX){
			throw new JedisException("设置锁等待时间或超时时间不符合要求");
		}
		long time = System.currentTimeMillis();
		long now = System.currentTimeMillis();
		boolean locked = getLock(cluster,lockKey,expire);
		while(!locked &&  now-time<waitTime*1000){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			locked = getLock(cluster,lockKey,expire);
			now = System.currentTimeMillis();
		}
		return locked;
	}

	
	/**
	  *  @Description    : 释放锁
	  *  @Method_Name    : freeLock
	  *  @param @param lockKey 锁名称
	  *  @return         : boolean   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年3月1日
	 */
	public boolean freeLock(String lockKey) {
		//Jedis jedis = RedisSinglePool.getInstance().getJedis();
		try {
			String lockValue = cluster.get(lockKey);
			if(StringUtils.isBlank(lockValue)){
				return true;
			}
			long deadLine = Long.valueOf(lockValue);
			long current = System.currentTimeMillis();
			if(deadLine>current){
				//此时说明其他线程已拿到锁，锁已经释放了
				return true;
			}
			long locked = cluster.del(lockKey);
			if (locked > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.info("freeLock(JedisClusterLock), lockKey"+lockKey);
		}finally{
//			try {
//				cluster.close();
//			} catch (IOException e) {
//			}
		}
		return false;
	}
	
	private boolean getLock(JedisCluster cluster,String lockKey,int expire){
		//setnx:SET if Not exists（如果不存在，则set）
		//如果lock存在，则设置失败 返回0
		//否则，设置成功 返回1
		long current = System.currentTimeMillis();
		long locked = cluster.setnx(lockKey,Long.toString(current+expire));
		//如果locked==1，表示获取到锁
		//如果locked=0，表示锁被其他客户端取得
		if(locked==1){
			long result =cluster.expire(lockKey,expire);
			return true;
		}
		return false;

	}
}
