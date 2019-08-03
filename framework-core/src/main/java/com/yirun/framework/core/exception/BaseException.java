package com.yirun.framework.core.exception;

/**
 * @Description   : 基类异常，只能通过继承使用
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.exception.BaseException.java
 * @Author        : imzhousong@gmail.com 周松
 */
public abstract class BaseException extends RuntimeException {

	private static final long serialVersionUID = 3081005071365603850L;

	/**
	 * 错误编码
	 */
	private String code;

	private Object[] values;

	/**
	 * 错误消息
	 */
	private String errorMsg;

	public BaseException() {
		super();
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(String message, Throwable cause, String code, Object[] values) {
		super(message, cause);
		this.code = code;
		this.values = values;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object[] getValues() {
		return this.values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}