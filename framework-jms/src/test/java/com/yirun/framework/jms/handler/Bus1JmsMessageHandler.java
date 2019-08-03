package com.yirun.framework.jms.handler;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.yirun.framework.jms.enums.DestinationType;

/**
 * @Described	: 业务模块接收处理jms信息，继承AbstractJmsMessageHandler重写handler****Message方法
 * @project		: com.yirun.framework.jms.listener.BusJmsMessageHandler
 * @author 		: zc.ding
 * @date 		: 2017年2月22日
 */
//@Component("bus1JmsMessageHandler")
public class Bus1JmsMessageHandler extends AbstractJmsMessageHandler{

	private final Logger logger = Logger.getLogger(Bus1JmsMessageHandler.class);
	
	/**
	 * consumer.prefetchSize=0 : 从queue中主动拉取数据，>0：表示broker主动推送数据
	 */
//	public static final String DEST_NAME = "BUS-TEST1?consumer.prefetchSize=0";
	public static final String DEST_NAME = "BUS-TEST1";
	public static final int DESTINATION_TYPE = DestinationType.QUEUE.getValue();
//	public static final int DESTINATION_TYPE = DestinationType.TOPIC.getValue();
	
//	初始化方法一
	public Bus1JmsMessageHandler(){
		super.setDestinations(DEST_NAME);
		super.setDestinationType(DESTINATION_TYPE);
	}
	
//	初始化方法二
	@Override
	public void setDestNameAndType() {
//		super.setDestinations(DEST_NAME);
//		super.setDestinationType(DESTINATION_TYPE);
	}
	
//	-----------初始化方法三------------------------------
	@Override
	public String getDestinations(){
		return DEST_NAME;
	}
	
	@Override
	public int getDestinationType(){
		return DESTINATION_TYPE;
	}
//	------------------------------------------------

	
	/**
	 * 重写父类中的方法
	 */
	public void handlerTextMessage(TextMessage textMessage) throws JMSException {
		logger.info("Bus1JmsMessageHandler监听单独的QUEUE或是TOPIC.....");
		logger.info("Bus1JmsMessageHandler业务模块中对JMS消息进行处理.....");
		logger.info(textMessage);
		logger.info(textMessage.getText());
	}
}
