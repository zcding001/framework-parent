package com.yirun.framework.jms.handler;

import com.yirun.framework.jms.JmsManagerI;
import com.yirun.framework.jms.JmsMessageHandler;
import com.yirun.framework.jms.enums.FailType;
import com.yirun.framework.jms.factory.CustContainerFactory;
import com.yirun.framework.jms.polling.JmsFailMsg;
import com.yirun.framework.jms.polling.RecoverJmsFailMsgI;
import com.yirun.framework.jms.utils.JmsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.AbstractPollingMessageListenerContainer;

import javax.jms.*;

public abstract class AbstractJmsMessageHandler implements JmsMessageHandler, JmsManagerI, InitializingBean{
	
	private final Logger logger = LoggerFactory.getLogger(AbstractJmsMessageHandler.class);
//	消息监听监听容器
	@Autowired
	CustContainerFactory custContainerFactory;
//	消息地址名称或是集合
	String destinations;
//	目的地址类型
	int destinationType;
//	是否接收Dead leader queue的消息
	boolean receiveDlq = false;
//	properties文件中配置的前缀名称
	String prefix;
	AbstractPollingMessageListenerContainer container;
	
	RecoverJmsFailMsgI recoverJmsFailMsgI;
	
	public AbstractJmsMessageHandler(){
		//设置destName和type
		this.setDestNameAndType();
	}
	
	/**
	 * @Described			: 设置消息的目的地址和消息的类型<br/>
	 * 							super.setDestinations(...)<br/>
	 *							super.setDestinationType(...);
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.handler.AbstractJmsMessageHandler.java
	 * @return				: void
	 * @date 				: 2017年4月13日
	 */
	public abstract void setDestNameAndType();
	
	public void setReceiveDlq(boolean receiveDlq) {
		this.receiveDlq = receiveDlq;
	}
	@Override
	public int getDestinationType() {
		return destinationType;
	}
	@Override
	public String getDestinations() {
		return destinations;
	}
	public void setDestinations(String destinations) {
		this.destinations = JmsUtils.getDestinations(destinations);
	}
	public void setDestinationType(int destinationType) {
		this.destinationType = destinationType;
	}
	public CustContainerFactory getCustContainerFactory() {
		return custContainerFactory;
	}
	public void setQkdContainerFactory(CustContainerFactory custContainerFactory) {
		this.custContainerFactory = custContainerFactory;
	}
	@Override
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
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
			logger.error("jms消息消费失败", e);
			JmsFailMsg jmsFailMsg = JmsUtils.fitJmsFailMsg(message, e, FailType.CONSUME);
			logger.error("消息目的地址：{}，消息目的地址类型：{}，消息体：{}", jmsFailMsg.getDestinationName(), jmsFailMsg.getDestinationType()
					.getValue(), jmsFailMsg.getMsg());
			//持久化消费异常的jms消息
			if(recoverJmsFailMsgI == null) {
				this.recoverJmsFailMsgI = JmsUtils.getRecoverJmsFailMsgI();
			}
			recoverJmsFailMsgI.addJmsFailMsg(jmsFailMsg);
		}
	}
	
	public void handlerTextMessage(TextMessage textMessage) throws JMSException {
		logger.info("消息体：{}", textMessage);
	}

	public void handlerObjectMessage(ObjectMessage objectMessage) throws JMSException {
		logger.info("消息体：{}", objectMessage);
	}

	public void handlerMapMessage(MapMessage mapMessage) throws JMSException {
		logger.info("消息体：{}", mapMessage);
	}

	public void handlerStreamMessage(StreamMessage streamMessage) throws JMSException {
		logger.info("消息体：{}", streamMessage);
	}

	public void handlerBytesMessage(BytesMessage bytesMessage) throws JMSException {
		logger.info("消息体：{}", bytesMessage);
	}
	
	@Override
	public int getTimeInterval() {
		return 0;
	}
	
	@Override
	public boolean isReceiveDlq() {
		return false;
	}
	
	@Override
	public AbstractPollingMessageListenerContainer initContainer() {
		return null;
	}
	
	@Override
	public void start() {
		if(container != null){
			container.initialize();
			container.start();
		}
	}

	@Override
	public void stop() {
		if(container != null)
			container.stop();
	}
	
	/**
	 * @Described			: 取消订阅
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.handler.AbstractJmsMessageHandler.java
	 * @return				: void
	 * @date 				: 2017年3月4日
	 * @throws Exception
	 */
	public void cancelSubscriber() throws JMSException{
		custContainerFactory.cancelSubscriber(container, this);
	}
	
	@Override
	public void afterPropertiesSet() {
		//判断业务是否重写了父类的接口，重新定义的监听容器
		container = initContainer();
		container = custContainerFactory.createListenerContainer(container, this);
	}
}
