package com.yirun.framework.jms.utils;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yirun.framework.jms.JmsService;
import com.yirun.framework.jms.impl.JmsServiceImpl;

public class TestJmsUtils {

	public static ApplicationContext context;
	
	public static JmsService jmsService;
	
	public static ActiveMQConnectionFactory jmsConnectionFactory;
	
	@BeforeClass
	public static void init(){
		if(context == null)
			context = new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext-jms.xml" });
		if(jmsService == null)
			jmsService = context.getBean(JmsServiceImpl.class); 
		if(jmsConnectionFactory == null){
            jmsConnectionFactory = context.getBean(ActiveMQConnectionFactory.class);
        }
	}
	
	@AfterClass
	public static void destory() {
	}
}
