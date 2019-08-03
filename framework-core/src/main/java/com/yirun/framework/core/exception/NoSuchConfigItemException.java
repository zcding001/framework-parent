package com.yirun.framework.core.exception;

/**
 * @Description   : 没有匹配的配置条目异常
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.exception.NoSuchConfigItemException.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class NoSuchConfigItemException extends GeneralException {

	private static final long serialVersionUID = 2539484905331343607L;

	public NoSuchConfigItemException() {
		super();
	}

	public NoSuchConfigItemException(String message) {
		super(message);
	}

	public NoSuchConfigItemException(Throwable cause) {
		super(cause);
	}

	public NoSuchConfigItemException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchConfigItemException(String message, Throwable cause, String code, Object[] values) {
		super(message, cause, code, values);
	}
}
