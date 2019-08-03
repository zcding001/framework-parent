package com.yirun.framework.jms.listener;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.log4j.Logger;

/**
 * @Description   : JMS异常监控
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.listener.JmsExceptionListener.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class JmsExceptionListener  implements ExceptionListener {

	private static Logger logger = Logger.getLogger(JmsExceptionListener.class);

	public void onException(JMSException paramJMSException) {
		logger.error(paramJMSException.getMessage(), paramJMSException);
	}

}
