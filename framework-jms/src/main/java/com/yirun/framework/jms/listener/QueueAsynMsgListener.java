package com.yirun.framework.jms.listener;

import java.util.concurrent.TimeUnit;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import com.yirun.framework.core.utils.PropertiesHolder;
import com.yirun.framework.jms.JmsMessageHandler;
import com.yirun.framework.jms.handler.DefaultJmsMessageHandler;
import com.yirun.framework.jms.utils.JmsUtils;

/**
 * @Description   : 异步消息监听
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.listener.QueueAsynMsgListener.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class QueueAsynMsgListener implements MessageListener, ApplicationContextAware, InitializingBean, Ordered {

	private static Logger logger = Logger.getLogger(QueueAsynMsgListener.class);

	private static JmsMessageHandler jmsMessageHandler;

	private ApplicationContext applicationContext = null;

	@Override
	public void onMessage(Message message) {
		try {
			if (jmsMessageHandler != null) {
				jmsMessageHandler.handlerMessage(message);
			} else {
				String lisImpl = PropertiesHolder.getProperty("mq.queue.listener.impl.class");
				if (StringUtils.isEmpty(lisImpl)) {
					jmsMessageHandler = BeanUtils.instantiateClass(DefaultJmsMessageHandler.class);
					logger.debug("init queue message listener!");
				} else {
					Class<?> clsImpl = Class.forName(lisImpl);
					jmsMessageHandler = getJmsMessageHandlerFromContext(applicationContext, clsImpl);
				}

				jmsMessageHandler.handlerMessage(message);
			}
			// sleep
			if (jmsMessageHandler.getTimeInterval() > 0) {
				TimeUnit.MILLISECONDS.sleep(jmsMessageHandler.getTimeInterval());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("QueueAsynMsgListener instance is created..........");
	}

	/**
	 *  @Description    : 从容器中获取bean
	 *  @Method_Name    : getJmsMessageHandlerFromContext
	 *  @param applicationContext	容器上下文
	 *  @param clsIml				类名
	 *  @return         : JmsMessageHandler
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public static JmsMessageHandler getJmsMessageHandlerFromContext(ApplicationContext applicationContext,
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
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE - 2;
	}

	public static void setBidMessageHandler(JmsMessageHandler jmsMessageHandler) {
		QueueAsynMsgListener.jmsMessageHandler = jmsMessageHandler;
	}
}
