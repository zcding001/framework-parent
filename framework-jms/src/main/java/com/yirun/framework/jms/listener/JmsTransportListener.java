package com.yirun.framework.jms.listener;

import java.io.IOException;

import org.apache.activemq.transport.TransportListener;
import org.apache.log4j.Logger;

/**
 * @Description   : 消息传输监听
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.listener.JmsTransportListener.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class JmsTransportListener implements TransportListener {
	
	private static Logger logger = Logger.getLogger(JmsTransportListener.class);

	@Override
	public void onCommand(Object paramObject) {
		logger.debug(paramObject);
	}

	@Override
	public void onException(IOException iOException) {
		logger.error(iOException.getMessage(), iOException);
	}

	@Override
	public void transportInterupted() {
	}

	@Override
	public void transportResumed() {
	}

}
