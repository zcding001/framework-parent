framework-jms是基于Framework的插件式工程，整合activeMQ，并提供简洁的调用方式。
包名声明：
destination：消息目的地址名称
enums：消息目的、消息类型的枚举
exceptions：jms异常的处理
impl：消息的核心实现类
listener：监听类
utils：framework-jms的工具类
引用方式，在业务工程的pom.xml文件下增加如下引用：
		<dependency>
			<groupId>com.yirun.framework</groupId>
			<artifactId>framework-jms</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
具体使用方式详见测试包下的单元测试。