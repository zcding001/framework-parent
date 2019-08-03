package com.yirun.framework.core.exception;
/**
 * 
 * @Description   : JDEDIS自定义异常类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.exception.JedisExpception.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public class JedisFrameworkExpception extends BaseException {

	private static final long serialVersionUID = 1L;

	public JedisFrameworkExpception() {
		super();
	}

	public JedisFrameworkExpception(String message) {
		super(message);
	}

	public JedisFrameworkExpception(Throwable cause) {
		super(cause);
	}

	public JedisFrameworkExpception(String message, Throwable cause) {
		super(message, cause);
	}

	public JedisFrameworkExpception(String message, Throwable cause, String code, Object[] values) {
		super(message, cause, code, values);
	}
}
