package com.yirun.framework.core.support.jta;

import java.lang.reflect.Method;

/**
 * @Description   : JTA事务model
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.support.jta.JTAModel.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class JTAModel {

	public JTAModel() {
	}

	public JTAModel(long startTime) {
		super();
		this.startTime = startTime;
	}

	public JTAModel(long startTime, Method method) {
		super();
		this.startTime = startTime;
		this.method = method;
	}

	private long startTime;

	private Method method;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
}
