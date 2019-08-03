package com.yirun.framework.jms.exceptions;

import com.yirun.framework.core.exception.BaseException;

/**
 * @Description   : JMS组件业务异常
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.exceptions.JmsException.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class JmsException extends BaseException {
	
	private static final long serialVersionUID = -4301298518851030589L;

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
