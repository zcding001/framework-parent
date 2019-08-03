package com.yirun.framework.jms.enums;

/**
 * @Description   : jms异常 消息类型
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.enums.FailType.java
 * @Author        : zhichaoding@hongkun.com zc.ding
 */
public enum FailType {

	SEND(1, "发送失败"),
	CONSUME(2, "消费失败");
	
	private final int value;
	private final String desc;
	private FailType(int value, String desc){
		this.desc = desc;
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	public String getDesc() {
		return desc;
	}
}
