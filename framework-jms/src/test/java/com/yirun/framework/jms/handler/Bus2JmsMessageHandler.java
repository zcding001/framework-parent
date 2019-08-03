package com.yirun.framework.jms.handler;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.yirun.framework.jms.enums.DestinationType;

/**
 * @Described	: 业务模块接收处理jms信息，继承AbstractJmsMessageHandler重写handler****Message方法
 * @project		: com.yirun.framework.jms.handler.Bus2JmsMessageHandler
 * @author 		: zc.ding
 * @date 		: 2017年3月4日
 */
//@Component("bus2JmsMessageHandler")
public class Bus2JmsMessageHandler extends AbstractJmsMessageHandler{

	private final Logger logger = Logger.getLogger(getClass());
	
	public static final String DEST_NAME = "BUS-TEST2, BUS-TEST2_1";
	public static final int DESTINATION_TYPE = DestinationType.QUEUE.getValue();
//	public static final int DESTINATION_TYPE = DestinationType.TOPIC.getValue();
	
	/**
	 * 从写父类中的方法
	 */
	public void handlerTextMessage(TextMessage textMessage) throws JMSException {
		logger.info("bus2JmsMessageHandler同时监听多个QUEUE或是TOPIC[BUS-TEST2, BUS-TEST2_1]");
		logger.info("bus2JmsMessageHandler业务2模块中对JMS消息进行处理.....");
		logger.info(textMessage);
		logger.info(textMessage.getText());
	}
	
	/**
	 * 从写父类中的方法
	 */
	public void handlerMapMessage(MapMessage mapMessage) throws JMSException {
		logger.info("业务2模块中对JMS消息进行处理.....");
		logger.info(mapMessage);
		 Enumeration<?> map = mapMessage.getMapNames();
		 while(map.hasMoreElements()){
			 String key = (String)map.nextElement();
			 logger.info("key : " + key + "\t value:" + mapMessage.getObject(key));
		 }
	}

	public String getDestinations(){
		return DEST_NAME;
	}
	
	public int getDestinationType(){
		return DESTINATION_TYPE;
	}

	@Override
	public void setDestNameAndType() {
		
	}
}

