package com.yirun.framework.redis.aop.aspectj;

import java.lang.reflect.Method;

import com.yirun.framework.redis.JedisClusterLock;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.yirun.framework.core.commons.Constants;
import com.yirun.framework.core.exception.JedisFrameworkExpception;
import com.yirun.framework.redis.annotation.DistributedLock;

@Aspect  
@Component("RedisLockInteceptor")  
@Order(1)
public class RedisLockInteceptor {
	
	@Around("@annotation(com.yirun.framework.redis.annotation.DistributedLock)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
		Signature signature=joinPoint.getSignature();
        MethodSignature methodSignature=(MethodSignature)signature;
        Method method=methodSignature.getMethod();
        DistributedLock disLock = AnnotationUtils.findAnnotation(method, DistributedLock.class);
		if(disLock==null){
			throw new JedisFrameworkExpception("RedisLockInteceptor==get disLock is null=====");
		}
		String lockKey = getDefKey(disLock, joinPoint.getArgs());
		int expireTime = disLock.expire();
		JedisClusterLock lock = new JedisClusterLock();
		//执行前上锁
		boolean lockFlag = lock.lock(lockKey, expireTime,Constants.LOCK_WAITTIME);
		Object result = null;
		if(lockFlag){
			result = joinPoint.proceed();
		}
		//执行后释放锁
		lock.freeLock(lockKey);
		return result;
	}
	
	
	/**
	  *  @Description    : 拼装key
	  *  @Method_Name    : getDefKey
	  *  @param @param disLock
	  *  @param @param args  （暂时不用此参数）
	  *  @param @return
	  *  @return         : String   
	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
	  *  @date			  ：2017年5月27日
	 */
	private String getDefKey(DistributedLock disLock, Object[] args) {
		String key = disLock.key();
		String prefix = disLock.prefix();
		if(StringUtils.isNotBlank(prefix)){
			return prefix + key;
		}
		return key;
	}
}
