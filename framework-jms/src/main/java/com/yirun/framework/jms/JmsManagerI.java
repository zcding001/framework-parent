package com.yirun.framework.jms;

import org.springframework.jms.listener.AbstractPollingMessageListenerContainer;

public interface JmsManagerI {

	/**
	 * @Described			: 解析获得的消息来自哪个目的地址
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsMessageHandler.java
	 * @return				: String
	 * @date 				: 2017年3月3日
	 * @return
	 */
	String getDestinations();
	
	/**
	 * @Described			: 通过接收到消息判断消息的目的地址的类型(queue/topic)
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsMessageHandler.java
	 * @return				: int
	 * @date 				: 2017年3月3日
	 * @return
	 */
	int getDestinationType();
	
	/**
	 * @Described			: 是否接收dead leader队列
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsDestinationI.java
	 * @return				: boolean
	 * @date 				: 2017年3月4日
	 * @return
	 */
	boolean isReceiveDlq();
	
	/**
	 * @Described			: 初始化监听容器
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsConfigI.java
	 * @return				: AbstractMessageListenerContainer
	 * @date 				: 2017年3月4日
	 * @return
	 */
	AbstractPollingMessageListenerContainer initContainer();
	
	/**
	 * @Described			: application_common_jms.properties中节点[mq.topic.test.receive.timeout=10000]
	 * 							中的topic.test
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsConfigI.java
	 * @return				: String
	 * @date 				: 2017年3月4日
	 * @return
	 */
	String getPrefix();
	
	/**
	 * @Described			: 启动监听
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsConfigI.java
	 * @return				: void
	 * @date 				: 2017年3月4日
	 */
	void start();
	
	/**
	 * @Described			: 停止监听
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsConfigI.java
	 * @return				: void
	 * @date 				: 2017年3月4日
	 */
	void stop();
}
