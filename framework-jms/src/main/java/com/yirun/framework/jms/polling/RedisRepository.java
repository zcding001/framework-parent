package com.yirun.framework.jms.polling;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.yirun.framework.core.commons.Constants;
import com.yirun.framework.core.model.ResponseEntity;
import com.yirun.framework.core.utils.CommonUtils;
import com.yirun.framework.core.utils.ObjectUtilsExtend;
import com.yirun.framework.core.utils.PropertiesHolder;
import com.yirun.framework.jms.JmsService;
import com.yirun.framework.jms.utils.JmsUtils;
import com.yirun.framework.redis.JedisUtils;

/**
 * @Description   : 将异常未消费的信息存储到redis中
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.polling.RedisRepository.java
 * @Author        : zhichaoding@hongkun.com zc.ding
 */
@Repository("redisRepositoryForJmsExceptionRecord")
public class RedisRepository implements RecoverJmsFailMsgI{
	
	private Logger logger = LoggerFactory.getLogger(RedisRepository.class);

	/**异常消息前缀*/
	private static final String PREFIX = "JMS";
	/**分隔符*/
	private static final String DELIMITER = ":";
	@Autowired
	private JmsService jmsService;
	
	@Override
	public Integer addJmsFailMsg(JmsFailMsg jmsFailMsg) {
		if(JedisUtils.set(getKey(jmsFailMsg.getCode()).getBytes(), ObjectUtilsExtend.objectToBytes(jmsFailMsg))) {
			return 1;
		}
		return 0;
	}

	@Override
	public ResponseEntity<?> delJmsFailMsg(List<String> codes) {
		if(codes != null) {
			codes.forEach(key -> {
				logger.info("删除存储的异常JMS消息，KEY：{}", getKey(key));
				JedisUtils.delete(getKey(key).getBytes());
			});
		}
		return new ResponseEntity<>(Constants.SUCCESS);
	}

	@Override
	public ResponseEntity<?> recoverJmsFailMsg(List<String> codes) {
		if(CommonUtils.isEmpty(codes)) {
			return new ResponseEntity<>(Constants.ERROR, "为找到需要恢复的JMS消息");
		}
		logger.info("需要恢复的JMS消息列表：{}", codes);
		codes.forEach(key -> {
			logger.info("重新发送JMS异常消息，KEY:{}", getKey(key));
			JmsFailMsg failMsg = ObjectUtilsExtend.bytesToObject(JedisUtils.get(getKey(key).getBytes()));
			jmsService.sendMsgForPolling(
					JmsUtils.getDestination(failMsg.getDestinationType(), failMsg.getDestinationName()),
					failMsg.getJmsMessageType(), failMsg.getMsg(), failMsg.getDeliveryMode(),
					failMsg.getPriority(), failMsg.getTimeToLive(), 1);
			this.delJmsFailMsg(Arrays.asList(failMsg.getCode()));
		});
		return new ResponseEntity<>(Constants.SUCCESS);
	}

	@Override
	public List<JmsFailMsg> findAllJmsFailMsg() {
		return JedisUtils.get(getKey("*"), JmsFailMsg.class);
	}

	/**
	 *  @Description    : 获得异常JMS消息的KEY
	 *  @Method_Name    : getKey
	 *  @return         : String
	 *  @Creation Date  : 2017年11月6日 下午3:30:45 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	private String getKey(String value) {
		String keywords = PropertiesHolder.getProperty(MQ_FIAL_MSG_STORAGE_KEYWORDS);
		if(StringUtils.isBlank(keywords)) {
			return PREFIX + DELIMITER + value;
		}
		return PREFIX + DELIMITER + keywords + DELIMITER + value;
	}
}
