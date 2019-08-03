package com.yirun.framework.jms.polling;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.yirun.framework.jms.enums.DestinationType;
import com.yirun.framework.jms.enums.JmsMessageType;

public class JmsFailMsg implements Serializable{

	
	/**
	 * serialVersionUID:
	 */
	    
	private static final long serialVersionUID = 1L;
	private Integer id;
	/**唯一标识**/
	private String code;
	/**目的地 */
	private String destinationName;
	/**消息类型**/
	private DestinationType destinationType;
	/**消息模式*/
	int deliveryMode;
	/**优先级*/
	int priority;
	/**消息存活时间*/
	long timeToLive;
	/**轮询次数*/
	int times = 1;
	/**消息类型*/
	private JmsMessageType jmsMessageType;
	/**消息内容*/
	private Object msg;
	/** 1: 发送失败 2：消费失败**/
	private Integer type;
	private Date creatTime;
	private Date modifyTime;
	
	/**异常消息内容**/
	private String exceptionMsg;
	
	
	public JmsFailMsg(){/****/}
	
	public JmsFailMsg(int deliveryMode, int priority, long timeToLive, int times){
		this.deliveryMode = deliveryMode;
		this.priority = priority;
		this.timeToLive = timeToLive;
		this.times = times;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getDeliveryMode() {
		return deliveryMode;
	}
	public void setDeliveryMode(int deliveryMode) {
		this.deliveryMode = deliveryMode;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public long getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}

	public JmsMessageType getJmsMessageType() {
		return jmsMessageType;
	}

	public void setJmsMessageType(JmsMessageType jmsMessageType) {
		this.jmsMessageType = jmsMessageType;
	}

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public DestinationType getDestinationType() {
		return destinationType;
	}
	public void setDestinationType(DestinationType destinationType) {
		this.destinationType = destinationType;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getExceptionMsg() {
		return exceptionMsg;
	}
	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
