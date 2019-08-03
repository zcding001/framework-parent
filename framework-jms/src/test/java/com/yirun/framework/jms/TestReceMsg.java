package com.yirun.framework.jms;

import javax.jms.Destination;
import javax.jms.Message;

import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

import com.yirun.framework.jms.commons.Constants;
import com.yirun.framework.jms.enums.DestinationType;
import com.yirun.framework.jms.handler.Bus1JmsMessageHandler;
import com.yirun.framework.jms.utils.JmsUtils;
import com.yirun.framework.jms.utils.TestJmsUtils;

public class TestReceMsg extends TestJmsUtils {
	private static String DEST_NAME = Constants.DEST_NAME;

	/**
	 * @Described			: 测试异步监听，只需要将com.yirun.framework.jms.handler包中的类的注解@Component("bus1JmsMessageHandler")取消注释即可
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.TestReceMsg.java
	 * @return				: void
	 * @date 				: 2017年3月4日
	 * @throws Exception
	 */
	@Test
	public void testWhileRece() throws Exception{
		while(true){
			Thread.sleep(1000);
		}
	}
	
	/**
	 * @Described			: 同步消费queue中消息
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.TestReceMsg.java
	 * @return				: void
	 * @date 				: 2017年2月22日
	 * @throws Exception
	 */
	@Test
	public void testReceMsg() throws Exception{
		JmsTemplate jmsTemplate = jmsService.getCurJmsTemplate();
		while(true){
			Message message = jmsTemplate.receive(DEST_NAME);
			Bus1JmsMessageHandler handler = new Bus1JmsMessageHandler();
			handler.handlerMessage(message);
			System.out.println("从queue中接收的信息：" + message);
		}
	}
	
	/**
	 * @Described			: 同步消费TOPIC中的消息
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.TestReceMsg.java
	 * @return				: void
	 * @date 				: 2017年2月22日
	 * @throws Exception
	 */
	@Test
	public void testRecMsgFromTopic() throws Exception{
		JmsTemplate jmsTemplate = jmsService.getCurJmsTemplate();
		Destination destination = JmsUtils.getDestination(DestinationType.TOPIC, DEST_NAME);
		while(true){
			Message message = jmsTemplate.receive(destination);
			Bus1JmsMessageHandler handler = new Bus1JmsMessageHandler();
			handler.handlerMessage(message);
			System.out.println("从TOPIC中接收的信息：" + message);
		}
	}
	
	/**
	 * @Described			: 测试取消订阅
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.TestReceMsg.java
	 * @return				: void
	 * @date 				: 2017年3月4日
	 */
	@Test
	public void testCancelSubscriber(){
		String clientId = "admin";
		String subName = "admin";
		jmsService.cancelSubscriber(clientId, subName);
	}
}
