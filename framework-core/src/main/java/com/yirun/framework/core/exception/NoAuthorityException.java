package com.yirun.framework.core.exception;

/**
 * @Description   : 无权限异常
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.exception.NoAuthorityException.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class NoAuthorityException extends SecurityExpception {

	private static final long serialVersionUID = -3028496821010699394L;

	public NoAuthorityException() {
		super();
	}

	public NoAuthorityException(String message) {
		super(message);
	}

	public NoAuthorityException(Throwable cause) {
		super(cause);
	}

	public NoAuthorityException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoAuthorityException(String message, Throwable cause, String code, Object[] values) {
		super(message, cause, code, values);
	}
}
