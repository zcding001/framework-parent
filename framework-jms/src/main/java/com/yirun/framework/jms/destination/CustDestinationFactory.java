package com.yirun.framework.jms.destination;


import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.commons.lang.StringUtils;

import com.yirun.framework.jms.JmsManagerI;
import com.yirun.framework.jms.enums.DestinationType;
import com.yirun.framework.jms.exceptions.JmsException;

/**
 * @Described	: 消息的Destination工厂
 * @project		: com.yirun.framework.jms.destination.DestinationFactory
 * @author 		: zc.ding
 * @date 		: 2017年3月4日
 */
public class CustDestinationFactory {

	/**
	 * @Described			: 初始化复合目的地址（多个queue或是多个topic）
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.destination.DestinationFactory.java
	 * @return				: ActiveMQDestination[]
	 * @date 				: 2017年3月4日
	 * @param qkdActiveMQI	Queue或是Topic实现类，用于获得具体的Desination实例
	 * @param desitions	目的地址字符串，多个地址中间用英文“,”分割
	 * @param receiveDlq	是否接收dead leader queue数据
	 * @return
	 */
	public static ActiveMQDestination[] getCompositeDestinations(CustActiveMQI qkdActiveMQI, String desitions, boolean receiveDlq){
		if (StringUtils.isEmpty(desitions)) {
			throw new JmsException("desition can't null!");
		}
		List<ActiveMQDestination> list = new ArrayList<ActiveMQDestination>();
		if (receiveDlq) {
			list.add(qkdActiveMQI.getDestination("ActiveMQ.DLQ"));
		}
		if (desitions.indexOf(",") != -1) {
			String [] ary = desitions.split(",");
			for (String name : ary) {
				if (StringUtils.isNotEmpty(name)) {
					list.add(qkdActiveMQI.getDestination(name.trim()));
				}       
			}
		} else {
			list.add(qkdActiveMQI.getDestination(desitions.trim()));
		}
		return list.toArray(new ActiveMQDestination[list.size()]);
	}

	/**
	 * @Described			: 创建监听的目的地址
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.destination.QkdDestinationFactory.java
	 * @return				: Destination
	 * @date 				: 2017年3月4日
	 * @param jmsMessageHandler 含有DestinationType和destination名称列表的消息handler
	 * @return
	 */
	public static Destination createDestination(JmsManagerI jmsDestinationI){
		String destName = jmsDestinationI.getDestinations();
		DestinationType destinationType = DestinationType.getEnum(jmsDestinationI.getDestinationType());
		return getDestination(destinationType, destName, jmsDestinationI.isReceiveDlq());
	}
	
	/**
	 * @Described			: 创建监听的目的地址
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.destination.DestinationFactory.java
	 * @return				: Destination
	 * @date 				: 2017年3月4日
	 * @param destinationType 监听地址类型
	 * @param destName	监听地址名称，多个地址中间用英文“,”分割
	 * @param receiveDlq
	 * @return
	 */
	public static Destination createDestination(DestinationType destinationType, String destName, boolean receiveDlq){
		return getDestination(destinationType, destName, receiveDlq);
	}
	
	/**
	 * @Described			: 初始化QkdActiveMQQueue
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.destination.DestinationFactory.java
	 * @return				: QkdActiveMQQueue
	 * @date 				: 2017年3月4日
	 * @param destName
	 * @param receiveDlq
	 * @return
	 */
	public static CustActiveMQQueue getQkdActiveMQQueue(String destName, boolean receiveDlq){
		CustActiveMQQueue queue = new CustActiveMQQueue();
		queue.setReceiveDlq(receiveDlq);
		queue.setDesitions(destName);
		return queue;
	}
	
	/**
	 * @Described			: 初始化QkdActiveMQTopic
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.destination.DestinationFactory.java
	 * @return				: QkdActiveMQTopic
	 * @date 				: 2017年3月4日
	 * @param destName
	 * @param receiveDlq
	 * @return
	 */
	public static CustActiveMQTopic getQkdActiveMQTopic(String destName, boolean receiveDlq){
		CustActiveMQTopic topic = new CustActiveMQTopic();
		topic.setReceiveDlq(receiveDlq);
		topic.setDesitions(destName);
		return topic;
	}
	
	/**
	 * @Described			: 
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.destination.QkdDestinationFactory.java
	 * @return				: Destination
	 * @date 				: 2017年3月4日
	 * @param destinationType
	 * @param destName
	 * @param receiveDlq
	 * @return
	 */
	public static Destination getDestination(DestinationType destinationType, String destName, boolean receiveDlq){
		switch (destinationType) {
		case QUEUE:
			return getQkdActiveMQQueue(destName, receiveDlq);
		case TOPIC:
			return getQkdActiveMQTopic(destName, receiveDlq);
		default:
			throw new JmsException(CustDestinationFactory.class.toString() + ":\t未找到监听的目的地址!");
		}
	}
}
