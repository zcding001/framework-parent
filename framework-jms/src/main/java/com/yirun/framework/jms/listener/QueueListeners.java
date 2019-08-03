package com.yirun.framework.jms.listener;

import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import com.yirun.framework.core.utils.PropertiesHolder;
import com.yirun.framework.jms.JmsMessageHandler;
import com.yirun.framework.jms.destination.CustActiveMQQueue;
import com.yirun.framework.jms.handler.DefaultJmsMessageHandler;
import com.yirun.framework.jms.utils.JmsUtils;

public class QueueListeners implements ApplicationContextAware, InitializingBean, Ordered {

	private static Logger logger = Logger.getLogger(QueueListeners.class);

	private ConnectionFactory connectionFactory;

	private CustActiveMQQueue custActiveMQQueue;

	private Connection connection = null;

	private boolean isAutoStart = true;

	private int sessionAcknowledgeMode = Session.AUTO_ACKNOWLEDGE;

	private ApplicationContext applicationContext = null;

	private static JmsMessageHandler jmsMessageHandler;

	public void init() throws JMSException {
		Session session = connection.createSession(false, sessionAcknowledgeMode);
		List<ActiveMQQueue> lists = custActiveMQQueue.getQueueLists();
		if (lists != null) {
			for (ActiveMQQueue activeMQQueue : lists) {
				if (StringUtils.isNotEmpty(activeMQQueue.getQueueName())
						&& activeMQQueue.getQueueName().contains(".DLQ")) {
					continue;
				}

				MessageConsumer messageConsumer = session.createConsumer(activeMQQueue);
				messageConsumer.setMessageListener(new MessageListener() {
					@Override
					public void onMessage(Message message) {
						messageHandler(message);
					}
				});
			}
		}
	}

	/**
	 *  @Description    : 消息处理
	 *  @Method_Name    : messageHandler
	 *  @param message
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	private void messageHandler(Message message) {
		try {
			if (jmsMessageHandler != null) {
				jmsMessageHandler.handlerMessage(message);
			} else {
				String lisImpl = PropertiesHolder.getProperty("mq.queue.listener.impl.class");
				if (StringUtils.isEmpty(lisImpl)) {
					jmsMessageHandler = getBidMessageHandlerFromContext(applicationContext,
							DefaultJmsMessageHandler.class);
					logger.debug("init queue message listener!");
				} else {
					Class<?> clsImpl = Class.forName(lisImpl);
					jmsMessageHandler = getBidMessageHandlerFromContext(applicationContext, clsImpl);
				}
				jmsMessageHandler.handlerMessage(message);
			}
			// message.acknowledge();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		connection = connectionFactory.createConnection();
		if (isAutoStart && connection != null) {
			init();
			connection.start();
		}
	}

	/**
	 *  @Description    : 从容器中获取bean
	 *  @Method_Name    : getBidMessageHandlerFromContext
	 *  @param applicationContext
	 *  @param clsImpl
	 *  @return         : JmsMessageHandler
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public static JmsMessageHandler getBidMessageHandlerFromContext(ApplicationContext applicationContext,
			Class<?> clsImpl) {
		try {
			String beanName = JmsUtils.firstWordToLowerCase(clsImpl.getSimpleName());

			if (applicationContext.containsBean(beanName)) {
				return (JmsMessageHandler) applicationContext.getBean(beanName, clsImpl);
			} else {
				return (JmsMessageHandler) applicationContext.getBean(clsImpl);
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE - 2;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public void setAutoStart(boolean isAutoStart) {
		this.isAutoStart = isAutoStart;
	}

	public void setQkdActiveMQQueue(CustActiveMQQueue custActiveMQQueue) {
		this.custActiveMQQueue = custActiveMQQueue;
	}

	public void setSessionAcknowledgeMode(int sessionAcknowledgeMode) {
		this.sessionAcknowledgeMode = sessionAcknowledgeMode;
	}
}
