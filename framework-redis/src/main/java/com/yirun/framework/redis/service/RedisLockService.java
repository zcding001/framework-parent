package com.yirun.framework.redis.service;
/**
 * 
 * @Description   : redis分布式锁----服务接口
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.service.RedisLockService.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public interface RedisLockService {
	/**
	  *  @Description    : 获取锁
	  *  @Method_Name    : lock
	  *  @param @param lockKey  锁名称
	  *  @param @param expire   超时时间:秒
	  *  @return         : boolean   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年3月1日
	 */
	public boolean lock(final String lockKey, final int expire);
	
	/**
	  *  @Description    : 释放锁
	  *  @Method_Name    : freeLock
	  *  @param @param lockKey 锁名称
	  *  @return         : boolean   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年3月1日
	 */
	public boolean freeLock(final String lockKey);
}
