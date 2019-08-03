package com.yirun.framework.redis.constants;

public interface CacheConstants {
	/** 默认刷新时间 单位：秒*/
	int CACHE_REFESH_TIME = 60;
	/** redis锁 等待时间 单位：毫秒*/
	int REDIS_LOCK_EXPIRE = 5000;
	/** redis-查询*/
	String REDIS_PREFIX = "REDIS_CACHE_"; 
	/** redis-lock */
	String REDIS_LOCK_PREFIX = "LOCK_"; 
	
}
