package com.yirun.framework.jms.utils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yirun.framework.jms.JmsService;
import com.yirun.framework.jms.impl.JmsServiceImpl;

public class TestSendJmsUtils {

	public static ApplicationContext context;
	
	public static JmsService jmsService;
	
	@BeforeClass
	public static void init(){
		if(context == null)
			context = new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext-jms4test.xml" });
		if(jmsService == null)
			jmsService = context.getBean(JmsServiceImpl.class); 
	}
	
	@AfterClass
	public static void destory() {
	}
}
