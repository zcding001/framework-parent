package com.yirun.framework.redis.constants;
/**
 * @Description   : redis缓存操作类型
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.constants.CacheOperType.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public enum CacheOperType {
	READ_WRITE, 
	
	WRITE, 
	
	DELETE,
	
	READ_ONLY;
}
