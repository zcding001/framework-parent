package com.yirun.framework.jms.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.Topic;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.ProducerCallback;

import com.yirun.framework.core.support.jta.JTAContext;
import com.yirun.framework.core.utils.NetUtils;
import com.yirun.framework.jms.JmsMessageHandler;
import com.yirun.framework.jms.JmsService;
import com.yirun.framework.jms.enums.DestinationType;
import com.yirun.framework.jms.enums.FailType;
import com.yirun.framework.jms.enums.JmsMessageType;
import com.yirun.framework.jms.polling.JmsPollingFailMsgServer;
import com.yirun.framework.jms.utils.JmsUtils;
import com.yirun.framework.redis.JedisUtils;

/**
 * @Description   : JMS消息生产者
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.impl.JmsServiceImpl.java
 * @Author        : imzhousong@gmail.com 周松
 */
// @Service
public class JmsServiceImpl  implements JmsService {

	private static Logger log = Logger.getLogger(JmsServiceImpl.class);

	// @Autowired
	private JmsTemplate jmsTemplate;
	private ConnectionFactory connectionFactory;
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public void setConnectionFactory(ConnectionFactory connectionFactory){
		this.connectionFactory = connectionFactory;
	}

	@Override
	public void sendMsg(Message message) {
		sendMsg(
				jmsTemplate.getDefaultDestination(), 
				message, 
				Message.DEFAULT_DELIVERY_MODE, 
				Message.DEFAULT_PRIORITY,
				Message.DEFAULT_TIME_TO_LIVE
				);
	}
	
	@Override
	public void sendMsg(String destnationName, DestinationType destinationType, Object message, JmsMessageType jmsMessageType) {
		Destination destination = JmsUtils.getDestination(destinationType, destnationName);
		sendMsgDetail(
				destination, 
				jmsMessageType, 
				message, 
				Message.DEFAULT_DELIVERY_MODE, 
				Message.DEFAULT_PRIORITY,
				Message.DEFAULT_TIME_TO_LIVE
				);
		
	}

	@Override
	public void sendMsg(Destination destination, Message message) {
		sendMsg(destination, message, Message.DEFAULT_DELIVERY_MODE, Message.DEFAULT_PRIORITY,
				Message.DEFAULT_TIME_TO_LIVE);
	}

	@Override
	public void sendMsg(Message message, int deliveryMode, int priority, long timeToLive) {
		sendMsg(jmsTemplate.getDefaultDestination(), message, deliveryMode, priority, timeToLive);
	}

	@Override
	public synchronized void sendMsg(final Destination destination, final Message message, final int deliveryMode,
			final int priority, final long timeToLive) {
		sendMsgDetail(destination, null, message, deliveryMode, priority, timeToLive);
	}

	@Override
	public synchronized void sendMsg(final Destination destination, final Serializable objMsg, final int deliveryMode,
			final int priority, final long timeToLive) {
		sendMsgDetail(destination, JmsMessageType.OBJECT, objMsg, deliveryMode, priority, timeToLive);
	}

	@Override
	public synchronized void sendMsg(final Destination destination, final String strMsg, final int deliveryMode,
			final int priority, final long timeToLive) {
		sendMsgDetail(destination, JmsMessageType.TEXT, strMsg, deliveryMode, priority, timeToLive);
	}

	@Override
	public synchronized void sendMsg(final Destination destination, final byte[] byteMsg, final int deliveryMode,
			final int priority, final long timeToLive) {
		sendMsgDetail(destination, JmsMessageType.BYTES, byteMsg, deliveryMode, priority, timeToLive);
	}

	@Override
	public synchronized void sendMsg(final Destination destination, final Map<String, Object> mapMsg,
			final int deliveryMode, final int priority, final long timeToLive) {
		sendMsgDetail(destination, JmsMessageType.MAP, mapMsg, deliveryMode, priority, timeToLive);
	}

	@Override
	public synchronized void sendMsg(final Destination destination, final InputStream streamMsg,
			final int deliveryMode, final int priority, final long timeToLive) {
		sendMsgDetail(destination, JmsMessageType.STREAM, streamMsg, deliveryMode, priority, timeToLive);
	}

	@Override
	public void sendTextMsg(String destinationName, String strMsg, DestinationType destType, int deliveryMode,
			int priority, long timeToLive) {
		Destination destination = JmsUtils.getDestination(destType, destinationName);
		sendMsg(destination, strMsg, deliveryMode, priority, timeToLive);
	}

	@Override
	public void sendObjectMsg(String destinationName, Serializable objMsg, DestinationType destType, int deliveryMode,
			int priority, long timeToLive) {
		Destination destination = JmsUtils.getDestination(destType, destinationName);
		sendMsg(destination, objMsg, deliveryMode, priority, timeToLive);
	}

	@Override
	public void sendBytesMsg(String destinationName, byte[] byteMsg, DestinationType destType, int deliveryMode,
			int priority, long timeToLive) {
		Destination destination = JmsUtils.getDestination(destType, destinationName);
		sendMsg(destination, byteMsg, deliveryMode, priority, timeToLive);
	}

	@Override
	public void sendMapMsg(String destinationName, Map<String, Object> mapMsg, DestinationType destType,
			int deliveryMode, int priority, long timeToLive) {
		Destination destination = JmsUtils.getDestination(destType, destinationName);
		sendMsg(destination, mapMsg, deliveryMode, priority, timeToLive);
	}
	
	@Override
	public void receDurableMsg(JmsMessageHandler handler, String clientId, String topicName, String subName) {
		try {
			Session session = getSession(clientId);
			MessageConsumer consumer = session.createDurableSubscriber(session.createTopic(topicName), subName);
			while(true){
				handler.handlerMessage(consumer.receive());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				close(clientId);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void receDurableMsg(JmsMessageHandler handler, String clientId, String topicName, String subName, String messageSelector, boolean flag) throws JMSException{
		Session session = getSession(clientId);
		MessageConsumer consumer = session.createDurableSubscriber(session.createTopic(topicName), subName, messageSelector, flag);
		handler.handlerMessage(consumer.receive());
	}
	
	@Override
	public void receAsynDurableMsg(MessageListener listener, String clientId, String topicName, String subName) throws JMSException{
		Session session = getSession(clientId);
		MessageConsumer consumer = session.createDurableSubscriber(session.createTopic(topicName), subName);
		consumer.setMessageListener(listener);
	}

	@Override
	public void receAsynDurableMsg(MessageListener listener, String clientId, String topicName, String subName, String messageSelector, boolean flag) throws JMSException{
		Session session = getSession(clientId);
		MessageConsumer consumer = session.createDurableSubscriber(session.createTopic(topicName), subName, messageSelector, flag);
		consumer.setMessageListener(listener);
	}
	
	
	@Override
	public void receAsynMsg(MessageListener listener ,String destinationName, DestinationType destinationType) throws JMSException{
		Session session = getSession();
		MessageConsumer consumer = session.createConsumer(JmsUtils.getDestination(destinationType, destinationName));
		consumer.setMessageListener(listener);
	}
	
	@Override
	public void receMsg(JmsMessageHandler handler, String destinationName, DestinationType destinationType) throws JMSException{
		Destination destination = JmsUtils.getDestination(destinationType, destinationName);
		Message message = jmsTemplate.receive(destination);
		handler.handlerMessage(message);
	}

	@Override
	public void cancelSubscriber(Session session, String subscriberName) {
		try {
			session.unsubscribe(subscriberName);
		} catch (Exception e) {
			log.error("JMS ERROR:" + e.getMessage(), e);
		}
	}
	
	@Override
	public void cancelSubscriber(String clientId, String subscriberName) {
		try {
			Session session = getSession(clientId);
			session.unsubscribe(subscriberName);
		} catch (Exception e) {
			log.error("JMS ERROR:" + e.getMessage(), e);
		}
	}

	@Override
	public void createSubscriber(Session session, Topic topic, String subscriberName) {
		try {
			session.createConsumer(topic, subscriberName);
		} catch (JMSException e) {
			log.error("JMS ERROR:" + e.getMessage(), e);
		}
	}

	@Override
	public JmsTemplate getCurJmsTemplate() {
		return this.jmsTemplate;
	}

	/**
	 * @Described			: 从JTA中获得Session，如果不存在创建新的Session并存储到JTA中
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.impl.JmsServiceImpl.java
	 * @return				: Session
	 * @date 				: 2017年2月8日
	 * @param params			非空：存放客户端注册的ClientId，用于创建持久性订阅消费者；空：用于创建消息创建者、消费者
	 * @return				: Session			jms消息会话
	 * @throws 				: JMSException
	 */
	Session getSession(String... params) throws JMSException {
		Session session = JTAContext.JMS_SESSION_LOCAL.get();
		if (session == null) {
			Connection con = getConnection(params);
			//此处考虑是否开始session的事务模式
			session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			JTAContext.JMS_SESSION_LOCAL.set(session);
		}
		return session;
	}

	/**
	 * @Described			: 发送消息的具体操作
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.impl.CustJmsServiceImpl.java
	 * @return				: void
	 * @date 				: 2017年2月10日
	 * @param destination 	消息目的地址名称
	 * @param messageType	消息类型
	 * @param object		消息内容
	 * @param deliveryMode	消息模式
	 * @param priority		消息优先级
	 * @param timeToLive	消息存活时间
	 * @throws 				: JMSException	
	 */
	void sendMsgDetail(Destination destination, JmsMessageType messageType, Object object, int deliveryMode, int priority, long timeToLive) {
		// 全局事务
		if (JTAContext.TRANSACTION_LOCAL.get() != null) {
				MessageProducer messageProducer = null;
				Message message = null;
				try {
					Session session = this.getSession();
					messageProducer = session.createProducer(destination);
					message = getSendMessage(session, destination, messageType, object, deliveryMode, priority, timeToLive);
					messageProducer.send(message);
				} catch (Exception e) {
					log.error("JMS ERROR:" + e.getMessage(), e);
					JedisUtils.lpush(JmsPollingFailMsgServer.FAIL_MQ_MESSAGE_POLL_POOL,
							JmsUtils.fitJmsFailMsg(message, e, 1, FailType.SEND));
				}finally{
					if (messageProducer != null) {
						try {
							messageProducer.close();
						} catch (JMSException e) {
							log.error("JMS ERROR:" + e.getMessage(), e);
						}
					}
				}
			
		} else {
			jmsTemplate.execute(new ProducerCallback<Object>() {
				public Object doInJms(Session session, MessageProducer messageProducer) {
					Message message = null;
					try {
						message = getSendMessage(session, destination, messageType, object, deliveryMode, priority, timeToLive);
						messageProducer.send(destination, message);
					} catch (Exception e) {
						log.error("JMS ERROR:" + e.getMessage(), e);
						JedisUtils.lpush(JmsPollingFailMsgServer.FAIL_MQ_MESSAGE_POLL_POOL,
								JmsUtils.fitJmsFailMsg(message, e, 1, FailType.SEND));
					}finally{
						if (messageProducer != null) {
							try {
								messageProducer.close();
							} catch (JMSException e) {
								log.error("JMS ERROR:" + e.getMessage(), e);
							}
						}
					}
					return null;
				}
			});
		}
	}
	
	/**
	 *  @Description    : 根据消息消息类型创建JMS消息
	 *  @Method_Name    : getSendMessage
	 *  @param session
	 *  @param destination
	 *  @param messageType
	 *  @param object
	 *  @param deliveryMode
	 *  @param priority
	 *  @param timeToLive
	 *  @return
	 *  @throws JMSException
	 *  @throws IOException
	 *  @return         : Message
	 *  @Creation Date  : 2017年11月21日 下午4:57:03 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	Message getSendMessage(Session session, Destination destination, JmsMessageType messageType, Object object, int deliveryMode, int priority, long timeToLive) throws JMSException, IOException {
		Message message = getSendMessage(session, messageType, object);
		message.setJMSDestination(destination);
		message.setJMSDeliveryMode(deliveryMode);
		message.setJMSPriority(priority);
		message.setJMSTimestamp(timeToLive);
		return message;
	}

	/**
	 * @Described			: 根据消息消息类型创建JMS消息
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.impl.JmsServiceImpl.java
	 * @return				: Message
	 * @date 				: 2017年2月10日
	 * @param session		JMS会话
	 * @param  				: messageType	消息类型
	 * @param  				: object		消息内容
	 * @throws 				: IOException 
	 */
	@SuppressWarnings("unchecked")
	Message getSendMessage(Session session, JmsMessageType messageType, Object object) throws JMSException, IOException{
		if(messageType == null || object instanceof Message){
			return (Message)object;
		}
		switch (messageType) {
		case BYTES:				//创建字节数组类型的消息
			BytesMessage message = session.createBytesMessage();
			message.writeBytes((byte[])object);
			return message;
		case MAP:				//创建Map类型的数据消息
			MapMessage mapMessage = session.createMapMessage();
			Map<String, Object> mapMsg = (Map<String, Object>)object;
			if (mapMsg != null) {
				for (Iterator<Map.Entry<String, Object>> it = mapMsg.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, Object> entry = it.next();
					mapMessage.setObject(entry.getKey(), entry.getValue());
				}
			}
			return mapMessage;
		case OBJECT:			//创建对象类型的消息
			ObjectMessage objectMessage = session.createObjectMessage((Serializable)object);
			objectMessage.setJMSMessageID(NetUtils.getLocalIp() + "_" + System.currentTimeMillis() + "_"
					+ System.nanoTime());
			return objectMessage;
		case STREAM:			//创建数据流类型的数据消息
			InputStream streamMsg = (InputStream)object;
			StreamMessage streamMessage = session.createStreamMessage();
			byte[] bytes = new byte[streamMsg.available()];
			streamMsg.read(bytes);
			streamMsg.close();
			streamMessage.writeBytes(bytes);
			return streamMessage;
		case TEXT:				//创建文本类型的数据消息
			return session.createTextMessage((String)object);
		default:
			break;
		}
		
		return null;
	}
	
	/**
	 * @Described			: 创建用于含有唯一客户注册ID的连接，多用于创建持久性消费之
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.impl.CustJmsServiceImpl.java
	 * @return				: Connection
	 * @date 				: 2017年2月13日
	 * @param params 非空：存放客户端注册的ClientId，用于创建持久性订阅消费者；空：用于创建消息创建者
	 * @throws 				: JMSException
	 */
	Connection getConnection(String... params) throws JMSException {
		Connection con = JTAContext.JMS_CONNECTION_LOCAL.get();
		if (con == null) {
			//设置客户注册时的唯一性的clientId，用于创建持久订阅消费者
			if(params != null && params.length > 0){
				//直接从connectionFactory中创建新的连接
				con = connectionFactory.createConnection();
				con.setClientID(params[0]);
				con.start();
			}else{
				ConnectionFactory cf = jmsTemplate.getConnectionFactory();
				con = cf.createConnection();
			}
			JTAContext.JMS_CONNECTION_LOCAL.set(con);
		}
		return con;
	}
	
	@Override
	public void close(String... params) throws JMSException{
		Connection conn = getConnection(params);
		if(conn != null){
			conn.close();
		}
	}
	
	@Override
	public void close(Connection conn) throws JMSException{
		if(conn != null){
			conn.close();
		}
	}
	
	@Override
	public synchronized void sendMsgForPolling(final Destination destination, JmsMessageType messageType, Object object, final int deliveryMode,
			final int priority, final long timeToLive, final int times) {
		sendMsgDetailForPolling(destination, messageType, object, deliveryMode, priority, timeToLive, times);
	}
	
	void sendMsgDetailForPolling(Destination destination, JmsMessageType messageType, Object object, int deliveryMode, int priority, long timeToLive, final int times) {
		// 全局事务
		if (JTAContext.TRANSACTION_LOCAL.get() != null) {
			MessageProducer messageProducer = null; 
			Message message = null;
			try {
				Session session = this.getSession();
				messageProducer = session.createProducer(destination);
				message = this.getSendMessage(session, destination, messageType, object, deliveryMode, priority, timeToLive);
//				producer.send(destination, message, deliveryMode, priority, timeToLive);
				messageProducer.send(message);
			} catch (Exception e) {
				log.error("JMS ERROR:" + e.getMessage(), e);
				JedisUtils.lpush(JmsPollingFailMsgServer.FAIL_MQ_MESSAGE_POLL_POOL,
						JmsUtils.fitJmsFailMsg(message, e, times, FailType.SEND));
			}finally{
				if (messageProducer != null) {
					try {
						messageProducer.close();
					} catch (JMSException e) {
						log.error("JMS ERROR:" + e.getMessage(), e);
					}
				}
			}
		} else {
			jmsTemplate.execute(new ProducerCallback<Object>() {
				public Object doInJms(Session session, MessageProducer messageProducer) {
					Message message = null;
					try {
						message = getSendMessage(session, destination, messageType, object, deliveryMode, priority, timeToLive);
						messageProducer.send(destination, message);
					} catch (Exception e) {
						log.error("JMS ERROR:" + e.getMessage(), e);
						JedisUtils.lpush(JmsPollingFailMsgServer.FAIL_MQ_MESSAGE_POLL_POOL,
								JmsUtils.fitJmsFailMsg(message, e, times, FailType.SEND));
					}finally{
						if (messageProducer != null) {
							try {
								messageProducer.close();
							} catch (JMSException e) {
								log.error("JMS ERROR:" + e.getMessage(), e);
							}
						}
					}
					return null;
				}
			});
		}
	}
}
