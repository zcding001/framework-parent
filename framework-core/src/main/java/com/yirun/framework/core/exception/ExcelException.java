package com.yirun.framework.core.exception;
/**
 * 
 * @Description   : excel导入导出异常类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.exception.ExcelException.java
 * @Author        : xuhuiliu@honghun.com.cn 刘旭辉
 */
public class ExcelException extends BaseException {
	
	    
	private static final long serialVersionUID = 1L;

	public ExcelException() {
		super();
	}

	public ExcelException(String message) {
		super(message);
	}

	public ExcelException(Throwable cause) {
		super(cause);
	}

	public ExcelException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExcelException(String message, Throwable cause, String code, Object[] values) {
		super(message, cause, code, values);
	}
	
}
