package com.yirun.framework.jms.handler;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.yirun.framework.jms.enums.DestinationType;

/**
 * @Described	: 业务模块接收处理jms信息，继承DefaultJmsMessageHandler重写handler****Message方法
 * @project		: com.yirun.framework.jms.listener.BusJmsMessageHandler
 * @author 		: zc.ding
 * @date 		: 2017年2月22日
 */
//@Component("bus4JmsMessageHandler")
public class Bus4JmsMessageHandler extends AbstractJmsMessageHandler {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void setDestNameAndType() {
		super.setDestinations("BUS-TEST4");
		super.setDestinationType(DestinationType.TOPIC.getValue());
		super.setPrefix("mq.topic.template");
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
	
	/**
	 * 从写父类中的方法
	 */
	public void handlerMapMessage(MapMessage mapMessage) throws JMSException {
		logger.info("业务3模块中对JMS消息进行处理.....");
		logger.info(mapMessage);
		 Enumeration<?> map = mapMessage.getMapNames();
		 while(map.hasMoreElements()){
			 String key = (String)map.nextElement();
			 logger.info("key : " + key + "\t value:" + mapMessage.getObject(key));
		 }
	}
}

