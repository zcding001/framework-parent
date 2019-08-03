package com.yirun.framework.redis.annotation;

import com.yirun.framework.redis.constants.CacheConstants;

/**
 * @Description   : 注解：分布式锁
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.annotation.DistributedLock.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public @interface DistributedLock {
	
	String key();
	/**
	  *  @Description    : 为key添加前缀，如果为null，加入默认前缀
	  *  @Method_Name    : prefix
	  *  @param @return
	  *  @return         : String   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月26日
	 */
	String prefix() default CacheConstants.REDIS_LOCK_PREFIX;
	/**
	  *  @Description    : 如果未获取到锁，等待时间  单位：毫秒
	  *  @Method_Name    : expire
	  *  @param @return
	  *  @return         : int   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月26日
	 */
	int expire() default CacheConstants.REDIS_LOCK_EXPIRE;
	
}
