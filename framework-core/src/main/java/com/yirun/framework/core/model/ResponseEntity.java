package com.yirun.framework.core.model;

import com.yirun.framework.core.commons.Constants;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description   : 响应信息
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.model.ResponseEntity.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class ResponseEntity<T> implements Serializable {

	private static final long serialVersionUID = -1483013122646260435L;

	/**
	 * 响应状态
	 */
	private int resStatus;

	/**
	 * 响应信息
	 */
	private T resMsg;

	/**
	 * 响应参数
	 */
	private Map<String, Object> params = new HashMap<String, Object>();

	public ResponseEntity() {
	}
	
	public ResponseEntity(int resStatus) {
		this.resStatus = resStatus;
	}

	public ResponseEntity(int resStatus, T resMsg) {
		super();
		this.resStatus = resStatus;
		this.resMsg = resMsg;
	}
	
	public ResponseEntity(int resStatus,T resMsg,Map<String, Object> params) {
		super();
		this.resStatus = resStatus;
		this.resMsg = resMsg;
		this.params = params;
	}

	/**
	 * 用于简单的状态返回
	 */
	public static final ResponseEntity SUCCESS = new ResponseEntity(Constants.SUCCESS);
	public static final ResponseEntity ERROR = new ResponseEntity(Constants.ERROR, "操作失败");

	public int getResStatus() {
		return resStatus;
	}

	public void setResStatus(int resStatus) {
		this.resStatus = resStatus;
	}

	public T getResMsg() {
		return resMsg;
	}

	public void setResMsg(T resMsg) {
		this.resMsg = resMsg;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	public ResponseEntity<T> addParam(String key, Object value) {
		this.params.put(key, value);
		return this;
	}
	
	/**
	 *  @Description    : 判断是不是成功状态
	 *  @Method_Name    : isSuccess
	 *  @return         : boolean
	 *  @Creation Date  : 2018年3月9日 下午2:15:24 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public boolean validSuc() {
		if(this.resStatus == Constants.SUCCESS)
			return true;
		return false;
	}
	
	/**
	 *  @Description    : 初始化错误对象
	 *  @Method_Name    : error
	 *  @param msg
	 *  @return         : ResponseEntity<?>
	 *  @Creation Date  : 2018年3月12日 上午11:30:37 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static ResponseEntity<?> error(Object msg){
		return new ResponseEntity<>(Constants.ERROR, msg);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
