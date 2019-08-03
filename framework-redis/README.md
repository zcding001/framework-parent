#framework-redis
说明：framework-redis是一个redis组件，提供了redis存取string、object、map、list等数据类型的方法和分布式锁的使用。
##使用方式：
pom文件：引入到你的项目中
        <dependency>
			<groupId>com.yirun.framework</groupId>
			<artifactId>framework-redis</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
##配置文件：
       将application-redis.properties加到项目的根目录，配置redis服务ip、端口等配置信息。
   application配置文件：
       添加注解配置
   <!-- 启用注解 -->
   <context:annotation-config />
   <!-- 扫描注解 -->
    <context:component-scan base-package="com.yirun.framework.redis.annotation"></context:component-scan>
    <context:component-scan base-package="com.yirun.framework.redis.aop.aspectj" />
    <!--开启代理 -->
    <aop:aspectj-autoproxy proxy-target-class="true" expose-proxy="true" />
##调用：
	推荐方法：通过注解调用
	缓存注解：@cache(key="",prefix="",expireTime="",refeshTime="",operType="")
	其中参数  key为必填项，是redis存储的key。
		   prefix为key的前缀，可选，默认为"REDIS_CACHE_"
		   expireTime为超时时间，单位：秒  默认300s
		   refeshTime为刷新时间，如果剩余时间<=refeshTime,刷新超时时间为初始值expireTime  默认：60s
		   operType为操作类型：有以下几个值    默认：CacheOperType.READ_WRITE
		   			CacheOperType.READ_WRITE  如果存在read后返回，如果不存在，write
		   			CacheOperType.DELETE      一般用于数据库更新后，删除缓存
		   			CacheOperType.READ_ONLY   只读，无论缓存中是否存在都不会操作写操作。
		   			CacheOperType.WRITE  暂时没有使用场景，预留。   		
		  另外：key如果使用“#0”标识，可以把注解的方法中的参数当作key，进行缓存。  #：使用参数的唯一标识 0：该方法的第几个参数，从0开始	
		  例如 方法：
		  @cache(key="#0",prefix="user_")
		  public String getUserName(String userName,int id){
		  }
		  调用时候
		 getUserName（"lxh",1）;
		  最后存入redis的key为：user_lxh ,如果自定义key，不想使用方法中的参数，不要用#开头。
	分布式锁注解@DistributedLock(key="",prefix="",expire="")
		其中参数  key为必填项，是redis存储的key。
			   prefix为key的前缀，可选，默认为"REDIS_LOCK_"
			   expire为等待时间，如果未获取到锁，等待时间  单位：毫秒 默认：5000ms
			   
			   
   
##其他使用方式
    1、redis存取操作：直接使用JedisUtils工具类做存取操作
    2、分布式锁：使用JedisLock工具类lock方法加锁，freeLock方法释放锁
具体方法参考测试类实现。