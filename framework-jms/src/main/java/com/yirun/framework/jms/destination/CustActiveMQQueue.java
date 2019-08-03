package com.yirun.framework.jms.destination;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;

import com.yirun.framework.jms.utils.JmsUtils;

 /**
 * @description:	消息队列
 */
public class CustActiveMQQueue extends ActiveMQQueue implements Queue, CustActiveMQI {
	
	private String desitions;
	
	private boolean isReceiveDlq = true;
	
	private List<ActiveMQQueue> queueLists = new ArrayList<>();
 
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

	public List<ActiveMQQueue> getQueueLists() {
		return queueLists;
	}

	@Override
	public ActiveMQDestination getDestination(String name) {
		return new ActiveMQQueue(JmsUtils.getDestinations(name)) ;
	}
}

