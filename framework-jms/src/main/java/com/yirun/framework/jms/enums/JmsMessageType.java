package com.yirun.framework.jms.enums;

/**
 * @Description   : 消息类别
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.enums.JmsMessageType.java
 * @Author        : imzhousong@gmail.com 周松
 */
public enum JmsMessageType {
	/**
	 * 文本、字节、流、MAP、对象
	 */
	TEXT(1), BYTES(2), STREAM(3), MAP(4), OBJECT(5);
	
	private final int value;
	
	JmsMessageType(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public static JmsMessageType getEnum(int value) {
		switch (value) {
		case 1:
			return TEXT;
		case 2:
			return BYTES;
		case 3:
			return STREAM;
		case 4:
			return MAP;
		default:
			return OBJECT;
		}
	}
}
