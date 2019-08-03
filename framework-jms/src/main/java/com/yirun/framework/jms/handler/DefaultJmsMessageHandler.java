package com.yirun.framework.jms.handler;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.yirun.framework.jms.JmsMessageHandler;

/**
 * @Description   : 消息处理默认实现
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.listener.DefaultJmsMessageHandler.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class DefaultJmsMessageHandler implements JmsMessageHandler {

	private final Logger logger = Logger.getLogger(DefaultJmsMessageHandler.class);
	
	@Override
	public synchronized void handlerMessage(Message message) {
		try {
			logger.info("receiveMsg:depiveryMode:" + message.getJMSDeliveryMode() + "\r\nmessageId:"
					+ message.getJMSMessageID() + "\r\ntimestamp:" + message.getJMSTimestamp() + "\r\n=================");
			if (message instanceof TextMessage) {
				handlerTextMessage((TextMessage) message);
			} else if (message instanceof ObjectMessage) {
				handlerObjectMessage((ObjectMessage) message);
			} else if (message instanceof MapMessage) {
				handlerMapMessage((MapMessage) message);
			} else if (message instanceof StreamMessage) {
				handlerStreamMessage((StreamMessage) message);
			} else if (message instanceof BytesMessage) {
				handlerBytesMessage((BytesMessage) message);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	protected void handlerTextMessage(TextMessage textMessage) throws JMSException {
		logger.info(textMessage);
	}

	protected void handlerObjectMessage(ObjectMessage objectMessage) throws JMSException {
		logger.info(objectMessage);
	}

	protected void handlerMapMessage(MapMessage mapMessage) throws JMSException {
		logger.info(mapMessage);
	}

	protected void handlerStreamMessage(StreamMessage streamMessage) throws JMSException {
		logger.info(streamMessage);
	}

	protected void handlerBytesMessage(BytesMessage bytesMessage) throws JMSException {
		logger.info(bytesMessage);
	}

	@Override
	public int getTimeInterval() {
		return 0;
	}

}
