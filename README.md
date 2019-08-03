Framework是一套分布式web快速开发框架。通过对spring、mybatis等优秀框架进行二次封装，最大限度地优化了web开发，提高了开发效率和及代码质量。
Framework无缝衔接SOA框架Dubbo。
Framework推荐微核+组件化开发。
目前已经有framework-jms(activeMQ)、framework-redis(redis)实现，framework-timer(quartz)、framework-security(spring security/OAuth)、framework-log4j待实现。

##Source Building
1、Checkout the framework source code:
	git clone http://123.57.253.218/songzhou/framework-parent.git framework-parent
2、Import the framework source code to eclipse project:
	mvn eclipse:eclipse	