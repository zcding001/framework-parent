package com.yirun.framework.jms.utils;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;

import com.yirun.framework.core.utils.ApplicationContextUtils;
import com.yirun.framework.core.utils.PropertiesHolder;
import com.yirun.framework.jms.destination.CustActiveMQQueue;
import com.yirun.framework.jms.destination.CustActiveMQTopic;
import com.yirun.framework.jms.enums.DestinationType;
import com.yirun.framework.jms.enums.FailType;
import com.yirun.framework.jms.enums.JmsMessageType;
import com.yirun.framework.jms.exceptions.JmsException;
import com.yirun.framework.jms.polling.JmsFailMsg;
import com.yirun.framework.jms.polling.RecoverJmsFailMsgI;

/**
 * @Description   : Jms工具类
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.utils.JmsUtils.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class JmsUtils {

	public static String firstWordToLowerCase(String word) {
		if (word.length() < 2) {
			throw new JmsException("class name length must greater than one!");
		}
		String firstWord = word.substring(0, 1).toLowerCase();
		String afterWork = word.substring(1, word.length());
		return firstWord + afterWork;
	}
	
	/**
	 *  @Description    : 组装jms异常消息
	 *  @Method_Name    : fitJmsFailMsg
	 *  @param message	: 消息内容
	 *  @param e		: 异常对象
	 *  @param times	: 发送失败的次数
	 *  @param failType	: 消息类型
	 *  @return         : JmsFailMsg
	 *  @Creation Date  : 2017年11月21日 下午2:36:31 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static JmsFailMsg fitJmsFailMsg(Message message, Exception e, int times, FailType failType) {
		JmsFailMsg failMsg = fitJmsFailMsg(message, e, failType);
		failMsg.setTimes(times);
		return failMsg;
	}
	
	/**
	 *  @Description    : 组装jms异常消息
	 *  @Method_Name    : fitJmsFailMsg
	 *  @param message	: 消息内容
	 *  @param e		: 异常对象
	 *  @param failType	: 消息类型
	 *  @return         : JmsFailMsg
	 *  @Creation Date  : 2017年11月21日 下午2:36:31 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static JmsFailMsg fitJmsFailMsg(Message message, Exception e, FailType failType) {
		JmsFailMsg failMsg = fitJmsFailMsg(message);
		//获得异常的详细信息
		StringBuffer sb = new StringBuffer(e.toString() + "\n");  
        StackTraceElement[] messages = e.getStackTrace();  
        int length = messages.length;  
        for (int i = 0; i < length; i++) {  
        	sb.append("\t"+messages[i].toString()+"\n");  
        }
        failMsg.setExceptionMsg(sb.toString());
        failMsg.setType(failType.getValue());
		return failMsg;
	}
	
	/**
	 *  @Description    : 封装失败的jms消息
	 *  @Method_Name    : fitJmsFailMsg
	 *  @param message
	 *  @throws JMSException
	 *  @return         : JmsFailMsg
	 *  @Creation Date  : 2017年10月31日 下午3:31:06 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	private static JmsFailMsg fitJmsFailMsg(Message message) {
		try {
			JmsFailMsg failMsg = new JmsFailMsg();
			failMsg.setCode(UUID.randomUUID().toString());
			failMsg.setDestinationName(getDestinationName(message.getJMSDestination()));
			failMsg.setDestinationType(getDestinationType(message.getJMSDestination()));
			failMsg.setDeliveryMode(message.getJMSDeliveryMode());
			failMsg.setPriority(message.getJMSPriority());
			failMsg.setTimeToLive(message.getJMSTimestamp());
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				failMsg.setJmsMessageType(JmsMessageType.TEXT);
				failMsg.setMsg(textMessage.getText());
			} else if (message instanceof ObjectMessage) {
				ObjectMessage objectMessage = (ObjectMessage) message;
				failMsg.setJmsMessageType(JmsMessageType.OBJECT);
				failMsg.setMsg(objectMessage.getObject());
			} else if (message instanceof MapMessage) {
				MapMessage mapMessage = (MapMessage) message;
				failMsg.setJmsMessageType(JmsMessageType.MAP);
				Enumeration<?> enums = mapMessage.getMapNames();
				Map<String, Object> map = new HashMap<>();
				while(enums.hasMoreElements()){
					Object key = enums.nextElement();
					map.put((String)key, mapMessage.getObject((String)key));
				}
				failMsg.setMsg(map);
			} else if (message instanceof StreamMessage) {
				StreamMessage streamMessage = (StreamMessage) message;
				byte[] buf = new byte[1024*1024];
				int length = streamMessage.readBytes(buf);
				byte[] result = Arrays.copyOf(buf, length);
				failMsg.setMsg(result);
			} else if (message instanceof BytesMessage) {
				BytesMessage bytesMessage = (BytesMessage) message;
				byte[] buf = new byte[(int)bytesMessage.getBodyLength()];
				bytesMessage.readBytes(buf);
				failMsg.setMsg(buf);
			}
			return failMsg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @Described			: 创建destionation消息目的地址
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.JmsService.java
	 * @return				: Destination
	 * @date 				: 2017年2月22日
	 * @param destinationName	目的地址名称
	 * @param destinationType	目的地址类型
	 * @return
	 */
	public static Destination getDestination(DestinationType destinationType, String destinationName){
		Destination destination = null;
		if (destinationType == DestinationType.QUEUE) {
//			destination = new ActiveMQQueue(destinationName);
			destination = new CustActiveMQQueue().getDestination(destinationName);
		} else if (destinationType == DestinationType.TOPIC) {
//			destination = new ActiveMQTopic(destinationName);
			destination = new CustActiveMQTopic().getDestination(destinationName);
		}
		return destination;
	}
	
	/**
	 *  @Description    : 获得目的地址名称
	 *  @Method_Name    : getDestinationName
	 *  @param destination
	 *  @return         : String
	 *  @Creation Date  : 2017年10月31日 下午4:03:50 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static String getDestinationName(Destination destination) {
		return ((ActiveMQDestination)destination).getPhysicalName();
	}
	
	/**
	 *  @Description    : 获得目的地址类型
	 *  @Method_Name    : getDestinationType
	 *  @param destination
	 *  @return         : DestinationType
	 *  @Creation Date  : 2017年10月31日 下午4:04:02 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static DestinationType getDestinationType(Destination destination) {
		if(destination instanceof ActiveMQTopic) {
			return DestinationType.TOPIC;
		}else {
			return DestinationType.QUEUE;
		}
	}
	
	/**
	 *  @Description    : 缓存异常mq的消息处理接口类型 1：jdbc，2：redis
	 *  @Method_Name    : getRecoverJmsFailMsgI
	 *  @return         : RecoverJmsFailMsgI
	 *  @Creation Date  : 2017年11月21日 下午2:40:46 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static RecoverJmsFailMsgI getRecoverJmsFailMsgI() {
		String type = PropertiesHolder.getProperty("mq.fail.msg.storage.type");
		if("2".equals(type)) {
			return ApplicationContextUtils.getBean("redisRepositoryForJmsExceptionRecord");
		}
		return ApplicationContextUtils.getBean("jdbcRepositoryForJmsExceptionRecord");
	}
	
	/**
	 *  @Description    : 处理目的地址前缀
	 *  @Method_Name    : getDestinations
	 *  @param destinations
	 *  @return         : String
	 *  @Creation Date  : 2018年3月15日 下午5:24:24 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static String getDestinations(String destinations) {
		String prefix = PropertiesHolder.getProperty("mq.desitions.prefix");
		prefix = prefix != null ? prefix : "";
		if(destinations != null && !destinations.startsWith(prefix)) {
			destinations = (prefix != null ? prefix : "") + destinations;
		}
		return destinations;
	}
	
	
	public static void main(String[] args) {
		System.out.println(getDestinationType(getDestination(DestinationType.TOPIC, "Hel")));;
	}
}
