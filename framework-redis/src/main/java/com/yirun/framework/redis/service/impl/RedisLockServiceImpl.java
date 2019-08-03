package com.yirun.framework.redis.service.impl;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.yirun.framework.redis.RedisSinglePool;
import com.yirun.framework.redis.service.RedisLockService;

public class RedisLockServiceImpl implements RedisLockService{
	private final Logger logger = Logger.getLogger(RedisLockServiceImpl.class);
	private RedisSinglePool singlePool;
	private Jedis jedis;
	public RedisLockServiceImpl(){
		singlePool = RedisSinglePool.getInstance();
		jedis = singlePool.getJedis();
	}
	public boolean lock(String lockKey, int expire) {
		//Jedis jedis = RedisSinglePool.getInstance().getJedis();
		long time = System.currentTimeMillis();
		long now = System.currentTimeMillis();
		boolean locked = getLock(jedis,lockKey,expire);
		while(!locked &&  now-time<expire*1000){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			locked = getLock(jedis,lockKey,expire);
			now = System.currentTimeMillis();
		}
		return locked;
	}

	public boolean freeLock(String lockKey) {
		//Jedis jedis = RedisSinglePool.getInstance().getJedis();
		try {
			long locked = jedis.del(lockKey);
			if (locked > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//释放redis链接
//			boolean returnFlag = singlePool.returnResource(jedis);
//			logger.info("returnFlag====="+returnFlag);
			jedis.close();
		}
		return false;
	}
	
	private boolean getLock(Jedis jedis,String lockKey,int expire){
		//setnx:SET if Not exists（如果不存在，则set）
		//如果lock存在，则设置失败 返回0
		//否则，设置成功 返回1
		long locked = jedis.setnx(lockKey,Long.toString(expire));
		//如果locked==1，表示获取到锁
		//如果locked=0，表示锁被其他客户端取得
		if(locked==1){
			long result =jedis.expire(lockKey,expire);
			return true;
		}
		return false;

	}

}
