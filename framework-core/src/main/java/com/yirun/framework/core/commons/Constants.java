package com.yirun.framework.core.commons;

/**
 * @Description : 框架常量类
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.commons.Constants.java
 * @Author : imzhousong@gmail.com 周松
 */
public final class Constants {

	public static final String EMPTY_STRING = "";

	public static final String SERVER_HOME = "server.home";

	public static final String SERVER_BASE = "server.base";

	public static final String SERVER_TOMCAT_BASE = "catalina.base";

	public static final String FRAMEWORK_PREFIX = "framework";

	public static final String MESSAGE_SOURCE = "messageSource";

	/**
	 * 分页相关
	 */
	public static final String PAGER = "pager";
	public static final String MESSAGE_SCOPE_NAME = "messages";

	public static final String PAGING_SCOPE_NAME = "paging";

	public static final String PAGING_CURRENT_PAGE_NUM = "currentPageNum";

	public static final String PAGING_PAGE_SIZE = "pageSize";

	/**
	 * WEB相关
	 */
	public static final String WEB_ACTION_REQUEST_METHOD_NAME = "WEB_ACTION_REQUEST_METHOD_NAME";

	public static final String WEB_ACTION_EXCEPTION_SCOPE_NAME = "exception";

	/**
	 * 前后台服务标识
	 */
	public static final String SERVER_TYPE = "server_type";
	/**
	 * 防重复提交的token
	 */
	public static final String WEB_SUBMIT_TOKEN = "submitToken";
	/**
	 * 0:token失效，1：token有效
	 */
	public static final String WEB_SUBMIT_TOKEN_STATUS = "submitTokenStatus";
	/** 存储submitToken值的前缀 **/
	public static final String WEB_SUBMIT_TOKEN_PREFIX = "SUBMIT_TOKEN:";

	public static final String WEB_VALIDATE_CODE = "validateCode";

	public static final String WEB_REQUEST_SCOPE_NAME = "request";

	public static final String WEB_SESSION_SCOPE_NAME = "session";

	public static final String WEB_APPLICATION_SCOPE_NAME = "application";

	public static final String WEB_CONTEXT_PATH = "contextPath";

	public static final String WEB_RESPONSE_SCOPE_NAME = "response";

	public static final String WEB_REQUEST_PARAMETER_MAP_NAME = "param";

	public static final String WEB_REQUEST_HEADER_MAP_NAME = "header";

	/**
	 * 静态资源服务器地址
	 */
	public static final String STATIC_HOST = "staticHost";

	/**
	 * 当前登陆用户
	 */
	public static final String CURRENT_USER = "CURRENT_USER";
	/**
	 * 0： session无效， 1:session有效
	 */
	public static final String CURRENT_USER_STAUTS = "CURRENT_USER_STATUS";

	/**
	 * 当前登陆用户授权信息
	 */
	public static final String CURRENT_USER_AUTH = "CURRENT_USER_AUTH";

	/**
	 * 排除字段
	 */
	public static final String EXECUTE_FIELDS = "EXECUTE_FIELDS";

	/**
	 * 包含字段
	 */
	public static final String INCLUDE_FIELDS = "INCLUDE_FIELDS";

	/**
	 * 应用名称
	 */
	public static final String APP_NAME = "app.name";

	/**
	 * 统一编码
	 */
	public static final String CHARSET_UTF8 = "UTF-8";

	/**
	 * 同步登录状态标识
	 */
	public static final String SYNC_LOGIN_STATE = "online";

	/**
	 * cookie凭证名称
	 */
	public static final String TICKET = "ticket";
	/**
	 * cookie凭证过期时间（秒）
	 */
	public static final int TICKET_EXPIRES_TIME = 1200;

	/**
	 * 成功 默认值200
	 */
	public static final int SUCCESS = 1000;
	/**
	 * 失败 默认值999
	 */
	public static final int ERROR = 999;

	/**
	 * 锁的前缀
	 */
	public static final String LOCK_PREFFIX = "LOCK_";
	/**
	 * 锁过期时间 默认 5s
	 */
	public static final int LOCK_EXPIRES = 5;
	/**
	 * 锁最大过期时间30s
	 */
	public static final int LOCK_EXPIRES_MAX = 30;
	/**
	 * 锁最小过期时间1s
	 */
	public static final int LOCK_EXPIRES_MIN = 1;
	/**
	 * 等待锁时间：5s
	 */
	public static final int LOCK_WAITTIME = 5;
	/**
	 * 等待锁最大时间：30s
	 */
	public static final int LOCK_WAITTIME_MAX = 30;
	/**
	 * 等待锁最小时间：1s
	 */
	public static final int LOCK_WAITTIME_MIN = 1;
	/**
	 * openapi与服务端交互的SESSIONID的key值
	 */
	public static final String SESSION_ID_KEY = "sessionId";
    /**
     * app端存于redis中session前缀
     */
	public static final String SESSION_ID_KEY_PREFIX = "SESSION:";
}
