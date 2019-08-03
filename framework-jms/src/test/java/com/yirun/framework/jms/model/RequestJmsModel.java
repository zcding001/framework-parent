package com.yirun.framework.jms.model;

import java.io.Serializable;

public class RequestJmsModel implements Serializable{

	private static final long serialVersionUID = -5050401944271324378L;

	/**
	 * 请求业务数据JSON格式【必填】
	 */
	private String requestJson;
	
	/**
	 * 请求实现类【必填】
	 */
	private Class<?> requestClazzType;
	
	/**
	 * 限制失败次数，当达到limitFailseNum的时候,消息自动销毁 ，默认0 不销毁
	 */
	private int limitFailseNum = 0;

	public String getRequestJson() {
		return requestJson;
	}

	public void setRequestJson(String requestJson) {
		this.requestJson = requestJson;
	}

	public Class<?> getRequestClazzType() {
		return requestClazzType;
	}

	public void setRequestClazzType(Class<?> requestClazzType) {
		this.requestClazzType = requestClazzType;
	}

	public int getLimitFailseNum() {
		return limitFailseNum;
	}

	public void setLimitFailseNum(int limitFailseNum) {
		this.limitFailseNum = limitFailseNum;
	}
	
}
