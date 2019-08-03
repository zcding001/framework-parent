package com.yirun.framework.jms;

import javax.jms.Message;

/**
 * @Description   : 消息处理接口
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.JmsMessageHandler.java
 * @Author        : imzhousong@gmail.com 周松
 */
public interface JmsMessageHandler{
	
	/**
	 *  @Description    : 消息处理方法
	 *  @Method_Name    : handlerMessage
	 *  @param message	消息
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public void handlerMessage(Message message);
	
	/**
	 *  @Description    : 设置接口消息时间间隔(在没有缓存MQ连接，session等情况下，为减轻服务器压力可以实现此方法 ,单位(ms))
	 *  @Method_Name    : getTimeInterval
	 *  @return         : int
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public int getTimeInterval();
}

