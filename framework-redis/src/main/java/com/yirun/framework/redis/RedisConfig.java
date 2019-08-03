package com.yirun.framework.redis;

import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * @Description   : redis参数配置
 * @Project       : framework-redis
 * @Program Name  : com.yirun.framework.redis.RedisConfig.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
@Component
public class RedisConfig {
	@Value("${redis.ip}")
	public String redis_ip;
	@Value("${redis.port}")
	public int  redis_port;
	@Value("${redis.password}")
	public String redis_password;
	/** 
	 * jedis的最大分配对象  控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取
	 * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
	 * */
	@Value("${redis.pool.maxActive}")
	public int redis_pool_maxActive;
	
    /** jEdis最大保存iDel(空闲)状态对象数*/ 
	@Value("${redis.pool.maxIdle}")
	public int redis_pool_maxIdle;
	
	/** jedis最大连接数*/ 
	@Value("${redis.pool.maxTotal}")
	public int redis_pool_maxTotal;
	
	/** jedis池没有对象返回时，最大等待时间 */ 
	@Value("${redis.pool.maxWait}")
	public int redis_pool_maxWait;
    
	/** jedis调用borrowObject方法时，是否进行有效检查 ，如果为true，则得到的jedis实例均是可用的 */
	@Value("${redis.pool.testOnBorrow}")
	public boolean redis_pool_testOnBorrow=true;
	
    /** jedis调用returnObject方法时，是否进行有效检查*/ 
	@Value("${redis.pool.testOnReturn}")
	public boolean redis_pool_testOnReturn=true;
	
	/** 锁的过期时间*/
	@Value("${redis.lock.timeout}")
	public long redis_lock_timeout=5000L;

	public String getRedis_ip() {
		return redis_ip;
	}

	public void setRedis_ip(String redis_ip) {
		this.redis_ip = redis_ip;
	}

	public int getRedis_port() {
		return redis_port;
	}

	public void setRedis_port(int redis_port) {
		this.redis_port = redis_port;
	}

	public String getRedis_password() {
		return redis_password;
	}

	public void setRedis_password(String redis_password) {
		this.redis_password = redis_password;
	}

	public int getRedis_pool_maxActive() {
		return redis_pool_maxActive;
	}

	public void setRedis_pool_maxActive(int redis_pool_maxActive) {
		this.redis_pool_maxActive = redis_pool_maxActive;
	}

	public int getRedis_pool_maxIdle() {
		return redis_pool_maxIdle;
	}

	public void setRedis_pool_maxIdle(int redis_pool_maxIdle) {
		this.redis_pool_maxIdle = redis_pool_maxIdle;
	}

	public int getRedis_pool_maxTotal() {
		return redis_pool_maxTotal;
	}

	public void setRedis_pool_maxTotal(int redis_pool_maxTotal) {
		this.redis_pool_maxTotal = redis_pool_maxTotal;
	}

	public int getRedis_pool_maxWait() {
		return redis_pool_maxWait;
	}

	public void setRedis_pool_maxWait(int redis_pool_maxWait) {
		this.redis_pool_maxWait = redis_pool_maxWait;
	}

	public boolean isRedis_pool_testOnBorrow() {
		return redis_pool_testOnBorrow;
	}

	public void setRedis_pool_testOnBorrow(boolean redis_pool_testOnBorrow) {
		this.redis_pool_testOnBorrow = redis_pool_testOnBorrow;
	}

	public boolean isRedis_pool_testOnReturn() {
		return redis_pool_testOnReturn;
	}

	public void setRedis_pool_testOnReturn(boolean redis_pool_testOnReturn) {
		this.redis_pool_testOnReturn = redis_pool_testOnReturn;
	}

	public long getRedis_lock_timeout() {
		return redis_lock_timeout;
	}

	public void setRedis_lock_timeout(long redis_lock_timeout) {
		this.redis_lock_timeout = redis_lock_timeout;
	}

	
	
}
