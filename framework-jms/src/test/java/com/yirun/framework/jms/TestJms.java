package com.yirun.framework.jms;

import javax.jms.DeliveryMode;
import javax.jms.Message;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yirun.framework.jms.enums.DestinationType;
import com.yirun.framework.jms.impl.JmsServiceImpl;

public class TestJms {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext-jms.xml" });
		JmsService jmsService = context.getBean(JmsServiceImpl.class);
		
		jmsService.sendTextMsg("TEST-QUEUE", "测试消息", DestinationType.QUEUE, DeliveryMode.PERSISTENT, 4, Message.DEFAULT_TIME_TO_LIVE);;
	}
	
}
