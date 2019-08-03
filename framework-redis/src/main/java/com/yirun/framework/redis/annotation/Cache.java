package com.yirun.framework.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yirun.framework.redis.constants.CacheConstants;
import com.yirun.framework.redis.constants.CacheOperType;

/**
 * @Description   : redis缓存注解
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.annotation.Cache.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
	/**
	  *  @Description    : 往redis中存放的key
	  *  
	  *  @Method_Name    : key
	  *  @param @return
	  *  @return         : String   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月26日
	 */
	String key();
	
	String prefix() default CacheConstants.REDIS_PREFIX;
	/**
	  *  @Description    : 超时时间   单位：秒  如果expire=0，不设置超时时间
	  *  @Method_Name    : expire
	  *  @param @return
	  *  @return         : int   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月26日
	 */
	int expireTime() default 300;
	/**
	  *  @Description    : 缓存默认刷新时间  60s
	  *  @Method_Name    : refreshTime
	  *  @param @return
	  *  @return         : int   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月27日
	 */
	int refreshTime() default CacheConstants.CACHE_REFESH_TIME;
	/**
	  *  @Description    : 是否刷新超时时间
	  *  @Method_Name    : refresh
	  *  @param @return
	  *  @return         : int   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月26日
	 */
	int refresh() default 1;
	/**
	  *  @Description    : 操作类型 默认：读写操作
	  *  @Method_Name    : operType
	  *  @param @return
	  *  @return         : int   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月27日
	 */
	CacheOperType operType() default CacheOperType.READ_WRITE;
}
