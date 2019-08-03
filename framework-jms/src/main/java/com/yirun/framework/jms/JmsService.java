package com.yirun.framework.jms;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;

import org.springframework.jms.core.JmsTemplate;

import com.yirun.framework.jms.enums.DestinationType;
import com.yirun.framework.jms.enums.JmsMessageType;

/**
 * @Description   : JMS消息操作类
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.JmsService.java
 * @Author        : imzhousong@gmail.com 周松
 */
public interface JmsService {

	/**
	 *  @Description    : 发送消息(消息destination从jmsTemplate取一个默认的消息目的地)s
	 *  @Method_Name    : sendMsg
	 *  @param message	待发送的消息
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : songzhou@hongkun.com.cn 周松
	 */
	public void sendMsg(Message message);
	
	/**
	 * @Described			: 发送消息(消息destinationName从jmsTemplate取一个默认的消息目的地)
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsService.java
	 * @return				: void
	 * @date 				: 2017年2月21日
	 * @param destinationName	目的地址名称
	 * @param destinationType	目的地址类型(TOPIC QUEUE)
	 * @param message			消息内容
	 * @param jmsMessageType	消息类型
	 * @throws 				: Exception
	 */
	public void sendMsg(String destinationName, DestinationType destinationType, Object message, JmsMessageType jmsMessageType);
	 
	/**
	 *  @Description    : 发送消息
	 *  @Method_Name    : sendMsg
	 *  @param destination	消息目的地址
	 *  @param message		待发送的消息
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMsg(Destination destination, Message message);

	/**
	 *  @Description    : 发送消息(消息destination从jmsTemplate取一个默认的消息目的地)
	 *  @Method_Name    : sendMsg
	 *  @param message		待发送的消息
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMsg(Message message, int deliveryMode, int priority, long timeToLive);
	
	/**
	 *  @Description    : 发送消息
	 *  @Method_Name    : sendMsg
	 *  @param destination	消息目的地址
	 *  @param message		待发送的消息
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMsg(Destination destination, Message message, int deliveryMode, int priority, long timeToLive);
	


	/**
	 *  @Description    : 发送文本消息
	 *  @Method_Name    : sendMsg
	 *  @param destination	消息目的地址
	 *  @param strMsg		待发送的文本消息
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMsg(Destination destination, String strMsg, int deliveryMode, int priority, long timeToLive);

	/**
	 *  @Description    : 发送文本消息
	 *  @Method_Name    : sendTextMsg
	 *  @param destinationName	消息目的地址名称
	 *  @param strMsg		待发送的文本消息
	 *  @param destType		消息类别
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendTextMsg(String destinationName, String strMsg, DestinationType destType, int deliveryMode,
			int priority, long timeToLive);

	/**
	 *  @Description    : 发送对象消息
	 *  @Method_Name    : sendMsg
	 *  @param destination	消息目的地址
	 *  @param objMsg		待发送的对象消息
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMsg(Destination destination, Serializable objMsg, int deliveryMode, int priority, long timeToLive);

	/**
	 *  @Description    : 发送对象消息
	 *  @Method_Name    : sendObjectMsg
	 *  @param destinationName	消息目的地址名称
	 *  @param objMsg		待发送的对象消息
	 *  @param destType		消息类别
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendObjectMsg(String destinationName, Serializable objMsg, DestinationType destType, int deliveryMode,
			int priority, long timeToLive);

	/**
	 *  @Description    : 发送字节消息
	 *  @Method_Name    : sendMsg
	 *  @param destination	消息目的地址
	 *  @param byteMsg		待发送的字节消息
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMsg(Destination destination, byte[] byteMsg, int deliveryMode, int priority, long timeToLive);

	/**
	 *  @Description    : 发送字节消息
	 *  @Method_Name    : sendBytesMsg
	 *  @param destinationName	消息目的地址名称
	 *  @param byteMsg		待发送的字节消息
	 *  @param destType		消息类别
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendBytesMsg(String destinationName, byte[] byteMsg, DestinationType destType, int deliveryMode,
			int priority, long timeToLive);

	/**
	 *  @Description    : 发送Map消息
	 *  @Method_Name    : sendMsg
	 *  @param destination	消息目的地址
	 *  @param mapMsg		待发送的Map消息
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMsg(Destination destination, Map<String, Object> mapMsg, int deliveryMode, int priority,
			long timeToLive);

	/**
	 *  @Description    : 发送Map消息
	 *  @Method_Name    : sendMapMsg
	 *  @param destinationName	消息目的地址名称
	 *  @param mapMsg		待发送的Map消息
	 *  @param destType		消息类别
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Creation Date  : 2016年6月20日 下午1:31:05 
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMapMsg(String destinationName, Map<String, Object> mapMsg, DestinationType destType,
			int deliveryMode, int priority, long timeToLive);

	/**
	 *  @Description    : 发送流消息
	 *  @Method_Name    : sendMsg
	 *  @param destination	消息目的地址
	 *  @param streamMsg	待发送的流消息
	 *  @param deliveryMode	传送模式:默认 PERSISTENT(DeliveryMode)
	 *  @param priority		消息优先级:默认的优先级是4
	 *  @param timeToLive	消息的存活周期(以毫秒为单位,0表示永不过期):默认的超时时间是没有限制的；消息永不过期
	 *  @throws Exception
	 *  @return         : void
	 *  @Creation Date  : 2016年6月20日 下午1:31:57 
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void sendMsg(Destination destination, InputStream streamMsg, int deliveryMode, int priority, long timeToLive);

	/**
	 *  @Description    : 取消持久订阅者
	 *  @Method_Name    : unSubscriber
	 *  @param session	JMS会话
	 *  @param subscriberName	持久订阅者名称
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void cancelSubscriber(Session session, String subscriberName);
	
	/**
	 * @Described			: 取消订阅（持久性）
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsService.java
	 * @return				: void
	 * @date 				: 2017年2月24日
	 * @param clientId	用户注册的id
	 * @param subscriberName	消费的订阅名称
	 */
	public void cancelSubscriber(String clientId, String subscriberName);

	/**
	 *  @Description    : 创建持久订阅者
	 *  @Method_Name    : createSubscriber
	 *  @param session	JMS会话
	 *  @param topic	消息目的地
	 *  @param subscriberName	持久订阅者名称
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void createSubscriber(Session session, Topic topic, String subscriberName);

	/**
	 *  @Description    : 返回JMS Template
	 *  @Method_Name    : getCurJmsTemplate
	 *  @return         : JmsTemplate
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public JmsTemplate getCurJmsTemplate();
	
	/**
	 * @Described			: 异步模式从broker中消费消息
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsService.java
	 * @return				: void
	 * @date 				: 2017年2月22日
	 * @param listener			消息异步处理接口
	 * @param destinationName	消息目的名称
	 * @param destinationType	消息目的类型
	 * @throws 				: JMSException
	 */
	public void receAsynMsg(MessageListener listener, String destinationName, DestinationType destinationType) throws JMSException;
	
	/**
	 * @Described			: 同步模式从broker中消费消息
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsService.java
	 * @return				: void
	 * @date 				: 2017年2月22日
	 * @param handler			消息处理接口，详细内容见{@link com.yirun.framework.jms.listener.DefaultJmsMessageHandler}
	 * @param destinationName	消息目的名称
	 * @param destinationType	消息目的类型
	 * @throws 				: JMSException
	 */
	public void receMsg(JmsMessageHandler handler, String destinationName, DestinationType destinationType) throws JMSException;

	/**
	 * @Described			: 同步消费topic中的消息(创建持久性订阅模式)
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.impl.CustJmsServiceImpl.java
	 * @return				: void
	 * @date 				: 2017年2月13日
	 * @param listener		收到消息的处理接口
	 * @param clientId		客户端注册的唯一ID
	 * @param topicName		主题名称
	 * @param subName		订阅名称
	 * @throws 				: JMSException
	 */
	public void receDurableMsg(JmsMessageHandler handler, String clientId, String topicName, String subName) throws JMSException;

	/**
	 * @Described			: 同步消费topic中的消息(创建持久性订阅模式)
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.impl.CustJmsServiceImpl.java
	 * @return				: void
	 * @date 				: 2017年2月13日
	 * @param listener		收到消息的处理接口
	 * @param clientId		客户端注册的唯一ID
	 * @param topicName		主题名称
	 * @param subName		订阅名称
	 * @throws 				: JMSException
	 */
	public void receDurableMsg(JmsMessageHandler handler, String clientId, String topicName, String subName, String messageSelector, boolean flag) throws JMSException;
	
	/**
	 * @Described			: 异步消费topic中的消息(创建持久性订阅模式)
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.impl.CustJmsServiceImpl.java
	 * @return				: void
	 * @date 				: 2017年2月13日
	 * @param listener		收到消息的处理接口
	 * @param clientId		客户端注册的唯一ID
	 * @param topicName		主题名称
	 * @param subName		订阅名称
	 * @throws 				: JMSException
	 */
	public void receAsynDurableMsg(MessageListener listener, String clientId, String topicName, String subName) throws JMSException;

	/**
	 * @Described			: 异步消费topic中的消息(创建持久性订阅模式)
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.impl.CustJmsServiceImpl.java
	 * @return				: void
	 * @date 				: 2017年2月13日
	 * @param listener				收到消息的处理接口
	 * @param clientId				客户端注册的唯一ID
	 * @param topicName				主题名称
	 * @param subName				订阅名称
	 * @param messageSelector		消息选择器
	 * @param flag					true 不接收与此相同的session或是connection发布的消息	
	 * @throws 				: JMSException
	 */
	public void receAsynDurableMsg(MessageListener listener, String clientId, String topicName, String subName, String messageSelector, boolean flag) throws JMSException;

	/**
	 * @Described			: 关闭connection连接
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsService.java
	 * @return				: void
	 * @date 				: 2017年3月6日
	 * @param params
	 * @throws JMSException
	 */
	public void close(String... params) throws JMSException;	

	/**
	 * @Described			: 关闭connection连接
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsService.java
	 * @return				: void
	 * @date 				: 2017年3月6日
	 * @param conn
	 * @throws JMSException
	 */
	public void close(Connection conn) throws JMSException;
	
	/**
	 *  @Description    : 轮询失败消息接口
	 *  @Method_Name    : sendMsgForPolling
	 *  @param destination
	 *  @param message
	 *  @param deliveryMode
	 *  @param priority
	 *  @param timeToLive
	 *  @param currTimes
	 *  @return         : void
	 *  @Creation Date  : 2017年7月11日 下午5:12:01 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public void sendMsgForPolling(final Destination destination, JmsMessageType messageType, Object object, final int deliveryMode,
			final int priority, final long timeToLive, final int times);
}
