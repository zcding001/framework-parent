package com.yirun.framework.jms.factory;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.commons.lang.StringUtils;
import org.springframework.jms.listener.AbstractPollingMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.yirun.framework.core.utils.PropertiesHolder;
import com.yirun.framework.jms.JmsManagerI;
import com.yirun.framework.jms.JmsMessageHandler;
import com.yirun.framework.jms.destination.CustDestinationFactory;
import com.yirun.framework.jms.enums.DestinationType;
import com.yirun.framework.jms.listener.CustMessageListerFactory;

/**
 * @Described	: 用于创建MessageListenerContainer的工厂类
 * @project		: com.yirun.framework.jms.factory.CustContainerFactory
 * @author 		: zc.ding
 * @date 		: 2017年3月3日
 */
public class CustContainerFactory {
	//jms连接工厂，可以用所有监听操作，建议只用于持久性订阅监听操作，因为使用jms连接池可省去对connection资源的管理
	private ConnectionFactory connectionFactory;
	/**
	 * jms连接池，用于监听队列和非持久性订阅，如果监听持久性操作只能用connectionFactory创建connection,<br/>
	 * 持久性性订阅需要在创建connection是设置ClientId,而连接池在项目启动时会先创建默认数量的Connection。<br/>
	 * 如果在调用connection.setClientId()接口会出现在启动的Connection设置ClientId的异常，因此持久性定义<br/>
	 * 使用ConnectionFacotry而不使用jmsConnectionPool
	 */
	private ConnectionFactory jmsConnectionPool;

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public ConnectionFactory getJmsConnectionPool() {
		return jmsConnectionPool;
	}

	public void setJmsConnectionPool(ConnectionFactory jmsConnectionPool) {
		this.jmsConnectionPool = jmsConnectionPool;
	}

	/**
	 * @Described			: 创建自定义的MessageListener容器
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: AbstractPollingMessageListenerContainer
	 * @date 				: 2017年3月4日
	 * @param container	自定义listener容器
	 * @param jmsMessageHandler	消息处理的handler
	 * @return
	 */
	public AbstractPollingMessageListenerContainer createListenerContainer(AbstractPollingMessageListenerContainer container, JmsMessageHandler jmsMessageHandler){
		AbstractPollingMessageListenerContainer containerTmp = container;
		if(containerTmp == null){
			containerTmp = new DefaultMessageListenerContainer();
		}
		initContainer(containerTmp, jmsMessageHandler);
		return containerTmp;
	}
	
	/**
	 * @Described			: 初始化MessageListener容器配置
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年3月4日
	 * @param container
	 * @param jmsMessageHandler
	 */
	private void initContainer(AbstractPollingMessageListenerContainer container, JmsMessageHandler jmsMessageHandler){
		if(((JmsManagerI)jmsMessageHandler).getDestinationType() == DestinationType.TOPIC.getValue()){
			container.setPubSubDomain(true);
		}
		this.setConnectionFactory(container);
		//加载监听listener
		container.setMessageListener(CustMessageListerFactory.createListener(jmsMessageHandler));
		//加载监听的目的地址
		container.setDestination(CustDestinationFactory.createDestination((JmsManagerI)jmsMessageHandler));
		//更新容器默认配置
		this.setDefaultConfig(container, jmsMessageHandler);
		//是自启动操作
		if(container.isAutoStartup()){
			container.initialize();
			container.start();
		}
	}
	
	/**
	 * @Described			: 根据订阅类型更新连接工厂源
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 */
	private void setConnectionFactory(AbstractPollingMessageListenerContainer container){
		if(container.isSubscriptionDurable()){
			container.setConnectionFactory(connectionFactory);
		}else{
			container.setConnectionFactory(jmsConnectionPool);
		}
	}
	
	/**
	 * @Described			: 通过解析properties中数据更新容器默认配置
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 * @param jmsMessageHandler
	 */
	private void setDefaultConfig(AbstractPollingMessageListenerContainer container, JmsMessageHandler jmsMessageHandler){
		String prefix = ((JmsManagerI)jmsMessageHandler).getPrefix();
		if(StringUtils.isNotEmpty(prefix)){
			//设置持久性订阅配置
			this.setSubscriptionDurable(container, prefix);
			//设置接收超时时间
			this.setReceiveTimeout(container, prefix);
			//设置session响应模式
			this.setSessionAcknowledgeMode(container, prefix);
			//设置缓存级别
			this.setCacheLevel(container, prefix);
			//设置自启动标志位
			this.setAutoStartup(container, prefix);
		}
	}
	
	/**
	 * @Described			: 持久性订阅配置
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 * @param prefix
	 */
	private void setSubscriptionDurable(AbstractPollingMessageListenerContainer container, String prefix){
		String subscriptionDurable = getValue(prefix + ".is.subscription.durable");
		//设置持久性订阅
		if(StringUtils.isNotEmpty(subscriptionDurable)){
			boolean flag = Boolean.parseBoolean(subscriptionDurable);
			container.setSubscriptionDurable(flag);
			if(flag){//如果是持久性
				//设置客户端clientId
				this.setClientId(container, prefix);
				//设置订阅者的名称
				this.setDurableSubscriptionName(container, prefix);
			}
		}
	}
	
	/**
	 * @Described			: 设置容器的客户端id
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 * @param prefix
	 */
	private void setClientId(AbstractPollingMessageListenerContainer container, String prefix){
		String clientId = getValue(prefix + ".listener.clientId");
		if(StringUtils.isNotEmpty(clientId)){
			container.setClientId(clientId);
		}
	}
	
	/**
	 * @Described			: 设置持久性订阅名称
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 * @param prefix
	 */
	private void setDurableSubscriptionName(AbstractPollingMessageListenerContainer container, String prefix){
		String durableSubscriptionName = getValue(prefix + ".desition.durable.subscriber.name");
		if(StringUtils.isNotEmpty(durableSubscriptionName)){
			container.setDurableSubscriptionName(durableSubscriptionName);
		}
	}
	
	/**
	 * @Described			: 设置接收消息超时时间
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 * @param prefix
	 */
	private void setReceiveTimeout(AbstractPollingMessageListenerContainer container, String prefix){
		String receiveTimeout = getValue(prefix + ".receive.timeout");
		if(StringUtils.isNotEmpty(receiveTimeout)){
			container.setReceiveTimeout(Long.parseLong(receiveTimeout));
		}
	}
	
	/**
	 * @Described			: 设置session响应模式
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 * @param prefix
	 */
	private void setSessionAcknowledgeMode(AbstractPollingMessageListenerContainer container, String prefix){
		String sessionAcknowledgeMode = getValue(prefix + ".session.acknowledge.mode");
		if(StringUtils.isNotEmpty(sessionAcknowledgeMode)){
			container.setSessionAcknowledgeMode(Integer.parseInt(sessionAcknowledgeMode));
		}
	}
	
	/**
	 * @Described			: 设置缓存级别
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 * @param prefix
	 */
	private void setCacheLevel(AbstractPollingMessageListenerContainer container, String prefix){
		String cacheLevel = getValue(prefix + ".cache.level");
		if(StringUtils.isNotEmpty(cacheLevel) && container instanceof DefaultMessageListenerContainer){
			((DefaultMessageListenerContainer)container).setCacheLevel(Integer.parseInt(cacheLevel));
		}
	}
	
	/**
	 * @Described			: 设置自启动标识
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年4月12日
	 * @param container
	 * @param prefix
	 */
	public void setAutoStartup(AbstractPollingMessageListenerContainer container, String prefix){
		String autoStartup = getValue(prefix + ".listener.auto.startup");
		if(StringUtils.isNotEmpty(autoStartup) && container instanceof DefaultMessageListenerContainer){
			container.setAutoStartup(Boolean.parseBoolean(autoStartup));
		}
	}
	
	/**
	 * @Described			: 通过工具类加载properties中的属性信息
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: String
	 * @date 				: 2017年3月4日
	 * @param key
	 * @return
	 */
	private String getValue(String key){
		return PropertiesHolder.getProperty(key);
	}
	
	/**
	 * @Described			: 取消订阅
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.factory.QkdContainerFactory.java
	 * @return				: void
	 * @date 				: 2017年3月4日
	 * @param container
	 * @param jmsMessageHandler
	 * @throws JMSException 
	 * @throws Exception
	 */
	public void cancelSubscriber(AbstractPollingMessageListenerContainer container, JmsMessageHandler jmsMessageHandler) throws JMSException{
		Connection conn = null;
		String destinations = ((JmsManagerI)jmsMessageHandler).getDestinations();
		String[] arr = destinations.split(",");
		for(String name : arr){
			if(StringUtils.isNotEmpty(name)){
				if(container.isSubscriptionDurable()){
					String clientId = container.getClientId();
					conn = connectionFactory.createConnection();
					conn.setClientID(clientId);
					conn.createSession(false, Session.AUTO_ACKNOWLEDGE).unsubscribe(name.trim());
				}else{
					conn = connectionFactory.createConnection();
					conn.createSession(false, Session.AUTO_ACKNOWLEDGE).unsubscribe(name.trim());
				}
			}
		}
		if(conn != null){
			conn.close();
		}
	}
}
