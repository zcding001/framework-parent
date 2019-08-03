package com.yirun.framework.core.exception;

/**
 * @Description   : 业务异常
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.exception.BusinessException.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class BusinessException extends BaseException {

	private static final long serialVersionUID = -4796404349526245258L;

	public BusinessException() {
		super();
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(String message, Throwable cause, String code, Object[] values) {
		super(message, cause, code, values);
	}

}
