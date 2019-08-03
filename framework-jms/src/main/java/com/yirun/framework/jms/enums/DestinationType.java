package com.yirun.framework.jms.enums;

/**
 * @Description   : 消息发布类别
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.enums.DestinationType.java
 * @Author        : imzhousong@gmail.com 周松
 */
public enum DestinationType {
	ANY(0),
	QUEUE(1),
	TOPIC(2);
	
	private final int value;
	DestinationType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public static DestinationType getEnum(int value){
		switch (value) {
		case 1:
			return QUEUE;
		case 2:
			return TOPIC;
		default:
			return ANY;
		}
	}
}
