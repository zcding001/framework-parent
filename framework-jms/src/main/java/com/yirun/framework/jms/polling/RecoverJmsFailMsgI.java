package com.yirun.framework.jms.polling;

import java.util.List;

import com.yirun.framework.core.model.ResponseEntity;

/**
 * @Description   : 处理由业务异常消费失败的jms消息
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.respository.CachableJmsException.java
 * @Author        : zhichaoding@hongkun.com zc.ding
 */
public interface RecoverJmsFailMsgI {
	
	/**
	 * jdbc:自定义存储异常消息的表名
	 * redis:存贮异常消息的KEY中的关键字
	 */
	static final String MQ_FIAL_MSG_STORAGE_KEYWORDS = "mq_fail_msg_storage_keywords";

	/**
	 *  @Description    : 存储jms处理异常的消息
	 *  @Method_Name    : addJmsFailMsg
	 *  @param message 异常的JMS消息
	 *  @return         : int
	 *  @Creation Date  : 2017年10月30日 下午3:35:29 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	Integer addJmsFailMsg(JmsFailMsg JmsFailMsg);
	/**
	 *  @Description    : 删除异常消息
	 *  @Method_Name    : delJmsFailMsg
	 *  @param codes
	 *  @return         : ResponseEntity<?>
	 *  @Creation Date  : 2017年10月30日 下午3:52:14 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	ResponseEntity<?> delJmsFailMsg(List<String> codes);
	
	/**
	 *  @Description    : 批量恢复消息
	 *  @Method_Name    : recoverJmsFailMsg
	 *  @param codes
	 *  @return         : ResponseEntity<?>
	 *  @Creation Date  : 2017年10月30日 下午3:57:30 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	ResponseEntity<?> recoverJmsFailMsg(List<String> codes);
	
	/**
	 *  @Description    : 检索所有异常消息
	 *  @Method_Name    : findAllJmsFailMsg
	 *  @return         : List<JmsFailMsg>
	 *  @Creation Date  : 2017年11月1日 上午10:41:21 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	List<JmsFailMsg> findAllJmsFailMsg();
}
