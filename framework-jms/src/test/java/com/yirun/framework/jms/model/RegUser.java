package com.yirun.framework.jms.model;

import java.io.Serializable;

public class RegUser implements Serializable {

	private static final long serialVersionUID = -6438361651950851337L;

	/**
	 * 描述:主键
	 * 字段: ID  INT(10) 
	 */
	private java.lang.Integer id;
	/**
	 * 描述:用户名
	 * 字段: USERNAME  VARCHAR(50) 
	 */
	private java.lang.String username;
	/**
	 * 描述:密码
	 * 字段: PASSWORD  VARCHAR(50) 
	 */
	private java.lang.String password;
	
	private java.util.Date createDate;
	
	public java.lang.Integer getId() {
		return id;
	}
	public void setId(java.lang.Integer id) {
		this.id = id;
	}
	public java.lang.String getUsername() {
		return username;
	}
	public void setUsername(java.lang.String username) {
		this.username = username;
	}
	public java.lang.String getPassword() {
		return password;
	}
	public void setPassword(java.lang.String password) {
		this.password = password;
	}
	public java.util.Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}
	
}
