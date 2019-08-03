package com.yirun.framework.jms.handler;


import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.jms.listener.AbstractPollingMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.yirun.framework.jms.enums.DestinationType;

/**
 * @Described	: 业务模块接收处理jms信息，继承DefaultJmsMessageHandler重写handler****Message方法
 * @project		: com.yirun.framework.jms.listener.BusJmsMessageHandler
 * @author 		: zc.ding
 * @date 		: 2017年2月22日
 */
@Component("bus3JmsMessageHandler")
public class Bus3JmsMessageHandler extends AbstractJmsMessageHandler {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void setDestNameAndType() {
		super.setDestinations("BUS-TEST3");
		super.setDestinationType(DestinationType.TOPIC.getValue());
	}
	
	/**
	 * 从写父类中的方法
	 */
	public void handlerTextMessage(TextMessage textMessage) throws JMSException {
		logger.info("Bus3JmsMessageHandler监听BUS-TEST3【持久性订阅】");
		logger.info("Bus3JmsMessageHandler业务3模块中对JMS消息进行处理.....");
		logger.info(textMessage);
		logger.info(textMessage.getText());
	}
	
	@Override
	public AbstractPollingMessageListenerContainer initContainer() {
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
//		设置监听topic
		container.setPubSubDomain(true);
//		设置为持久性订阅
		container.setSubscriptionDurable(true);
//		甚至客户端注册ID
		container.setClientId("yrtz_client_id");
//		设置订阅者名称
		container.setDurableSubscriptionName("yrtz_client_name");
//		设置接收超超时时间
		container.setReceiveTimeout(10000);
		return container;
	}

}

