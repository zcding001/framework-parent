package com.yirun.framework.core.exception;

/**
 * @Description   : JMS异常
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.exception.JmsException.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class JmsException extends BaseException {

	private static final long serialVersionUID = 6166805485791565894L;

	public JmsException() {
		super();
	}

	public JmsException(String message) {
		super(message);
	}

	public JmsException(Throwable cause) {
		super(cause);
	}

	public JmsException(String message, Throwable cause) {
		super(message, cause);
	}

	public JmsException(String message, Throwable cause, String code, Object[] values) {
		super(message, cause, code, values);
	}

}
