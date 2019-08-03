package com.yirun.framework.redis.aop.aspectj;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//import com.yirun.framework.redis.JedisUtils;
import com.yirun.framework.redis.JedisClusterUtils;
import com.yirun.framework.redis.annotation.Cache;
import com.yirun.framework.redis.constants.CacheOperType;

/**
 * 
 * @Description   : 缓存切面
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.aop.aspectj.CacheAspectInteceptor.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
@Aspect
@Component("CacheAspectInteceptor")  
@Order(1)
public class CacheAspectInteceptor {
	private static final Logger LOGGER = Logger.getLogger(CacheAspectInteceptor.class);
	
	@Around("@annotation(com.yirun.framework.redis.annotation.Cache)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
		Signature signature=joinPoint.getSignature();
        MethodSignature methodSignature=(MethodSignature)signature;
        Method method=methodSignature.getMethod();
		Cache cache = AnnotationUtils.findAnnotation(method, Cache.class);
		if(cache==null||StringUtils.isBlank(cache.key())){
			return joinPoint.proceed();
		}
		Object result = null ;
		LOGGER.debug("=====CacheAspectInteceptor=====key:"+cache.key());
		String key = cache.prefix() + analyzeKey(cache.key(), joinPoint.getArgs()); 
		if(CacheOperType.READ_WRITE == cache.operType()){
			result = JedisClusterUtils.getObjectForJson(key,new TypeReference<T>() {
				@Override
				public Type getType() {
					return method.getGenericReturnType();
				}
			});
			if(result!=null){
				//如果剩余时间小于等于默认刷新时间，刷新缓存
				if(cache.expireTime()> 0 && JedisClusterUtils.getRemainTime(key)<=cache.refreshTime()*1000){
					JedisClusterUtils.setExpireTime(key, cache.expireTime());
				}
				return result;
			}
			Object methodResult = joinPoint.proceed();
			asynWriteCache(key,methodResult,cache.expireTime());
			return methodResult;
		}
		if(CacheOperType.READ_ONLY == cache.operType()){
			return JedisClusterUtils.getObjectForJson(key,new TypeReference<T>() {
				@Override
				public Type getType() {
					return method.getGenericReturnType();
				}
			});
		}
		if(CacheOperType.DELETE == cache.operType()){
			Object methodResult = joinPoint.proceed();
			JedisClusterUtils.delete(key);
			return methodResult;
		}
		return joinPoint.proceed();
	}	
	
	
	private String analyzeKey(String key,Object[] args){
		String result = "";
		if(key.startsWith("#")){
			String keyIds = key.split("#")[1];
			if(StringUtils.isNotBlank(keyIds)){
				String[] keyArray = keyIds.split(",");
				for(String s:keyArray){
					Object r = args[Integer.parseInt(s)];
					if(r!=null){
						result = result + String.valueOf(r);
					}
				}
			}
		}
		return result;
	}
	/**
	  *  @Description    : 异步写缓存操作
	  *  @Method_Name    : asynWriteCache
	  *  @param 
	  *  @return         : void   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月31日
	 */
	private void asynWriteCache(String key,Object value,int expire){
		Thread t = new Thread(){
				public void run(){
					if(expire>0){
						JedisClusterUtils.setAsJson(key, value, expire);
					}else{
						JedisClusterUtils.setAsJson(key, value, JedisClusterUtils.NOT_EXPIRE);
					}
				}
		};
		t.start();
	}
}
