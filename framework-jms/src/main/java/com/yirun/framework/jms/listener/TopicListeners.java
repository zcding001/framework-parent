package com.yirun.framework.jms.listener;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import com.yirun.framework.core.utils.PropertiesHolder;
import com.yirun.framework.jms.JmsMessageHandler;
import com.yirun.framework.jms.handler.DefaultJmsMessageHandler;
import com.yirun.framework.jms.utils.JmsUtils;

public class TopicListeners implements ApplicationContextAware, InitializingBean, Ordered {

	private static Logger logger = Logger.getLogger(TopicListeners.class);

	private ConnectionFactory connectionFactory;

	private Topic destination;

	private Connection connection = null;

	private boolean isAutoStart = true;

	private int sessionAcknowledgeMode = Session.AUTO_ACKNOWLEDGE;

	private String clientId = "clientId-" + System.currentTimeMillis();

	/**
	 * 订阅者名称
	 */
	private String durableSubscriptionName = "";

	/**
	 * 是否持久订阅者
	 */
	private boolean isDurableSubscriber = false;

	private ApplicationContext applicationContext = null;

	private static JmsMessageHandler jmsMessageHandler;

	public void init() throws JMSException {
		Session session = connection.createSession(false, sessionAcknowledgeMode);
		MessageConsumer messageConsumer = null;
		if (isDurableSubscriber) {
			messageConsumer = session.createDurableSubscriber(destination, durableSubscriptionName);
		} else {
			messageConsumer = session.createConsumer(destination);
		}
		messageConsumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				messageHandler(message);

			}
		});

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
				String lisImpl = PropertiesHolder.getProperty("mq.topic.listener.impl.class");
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
		if (connection != null) {
			connection.setClientID(clientId);
		}
		if (isAutoStart) {
			init();
			connection.start();
		}
	}

	/**
	 * 
	 * @description 从容器中获取bean
	 * @param applicationContext
	 * @param clsIml
	 * @return
	 */
	public static JmsMessageHandler getBidMessageHandlerFromContext(ApplicationContext applicationContext,
			Class<?> clsIml) {
		try {

			String beanName = JmsUtils.firstWordToLowerCase(clsIml.getSimpleName());

			if (applicationContext.containsBean(beanName)) {
				return (JmsMessageHandler) applicationContext.getBean(beanName, clsIml);
			} else {
				return (JmsMessageHandler) applicationContext.getBean(clsIml);
			}
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE - 3;
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

	public void setSessionAcknowledgeMode(int sessionAcknowledgeMode) {
		this.sessionAcknowledgeMode = sessionAcknowledgeMode;
	}

	public void setDestination(Topic destination) {
		this.destination = destination;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setDurableSubscriptionName(String durableSubscriptionName) {
		this.durableSubscriptionName = durableSubscriptionName;
	}

	public void setDurableSubscriber(boolean isDurableSubscriber) {
		this.isDurableSubscriber = isDurableSubscriber;
	}

}
