package com.yirun.framework.core.exception;

/**
 * @Description   : 安全异常
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.exception.SecurityExpception.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class SecurityExpception extends BaseException {

	private static final long serialVersionUID = 8985547550244289493L;

	public SecurityExpception() {
		super();
	}

	public SecurityExpception(String message) {
		super(message);
	}

	public SecurityExpception(Throwable cause) {
		super(cause);
	}

	public SecurityExpception(String message, Throwable cause) {
		super(message, cause);
	}

	public SecurityExpception(String message, Throwable cause, String code, Object[] values) {
		super(message, cause, code, values);
	}
}
