package com.yirun.framework.jms.destination;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.InitializingBean;

import com.yirun.framework.jms.utils.JmsUtils;

/**
 * @Described	: 消息队列扩展，通过desitions同时向多个topic目的地发送消息
 * @project		: com.yirun.framework.jms.destination.QkdActiveMQTopic
 * @author 		: zc.ding
 * @date 		: 2017年3月2日
 */
public class CustActiveMQTopic extends ActiveMQTopic implements Topic, InitializingBean, CustActiveMQI{

	private String desitions;
	
	private boolean isReceiveDlq = true;
	
	private List<ActiveMQTopic> queueLists = new ArrayList<>();
	
	public String getDesitions() {
		return desitions;
	}

	public void setDesitions(String desitions) {
		this.desitions = JmsUtils.getDestinations(desitions);
		this.setCompositeDestinations(CustDestinationFactory.getCompositeDestinations(this, this.desitions, isReceiveDlq));
	}

	public boolean isReceiveDlq() {
		return isReceiveDlq;
	}

	public void setReceiveDlq(boolean isReceiveDlq) {
		this.isReceiveDlq = isReceiveDlq;
	}
	
	public List<ActiveMQTopic> getQueueLists() {
		return queueLists;
	}

	public void setQueueLists(List<ActiveMQTopic> queueLists) {
		this.queueLists = queueLists;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		//
	}

	@Override
	public ActiveMQDestination getDestination(String name) {
		return new ActiveMQTopic(JmsUtils.getDestinations(name));
	}
}
