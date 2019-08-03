package com.yirun.framework.jms.polling;

import com.yirun.framework.core.utils.PropertiesHolder;
import com.yirun.framework.jms.JmsService;
import com.yirun.framework.jms.utils.JmsUtils;
import com.yirun.framework.redis.JedisClusterUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class JmsPollingFailMsgServer implements InitializingBean {

	private static Logger log = Logger.getLogger(JmsPollingFailMsgServer.class);

	/**
	 * 失败的mq消息的轮询池
	 */
	public static final String FAIL_MQ_MESSAGE_POLL_POOL = "fial_mq_message_poll_pool";
	/**
	 * 需要手动处理mq失败消息池
	 */
//	public static final String FAIL_MQ_MESSAGE_MANUAL_POOL = "fial_mq_message_manual_pool";
	/**
	 * 尝试轮询次数2次
	 */
	private static final int DEFAULT_TIMES = 2;
	/**
	 * 默认轮询间隔时间5秒
	 */
	private static final long DEFAULT_SLEEP_TIME = 5000;

	@Autowired
	private JmsService jmsService;
	private RecoverJmsFailMsgI recoverJmsFailMsgI;

	private int times = DEFAULT_TIMES;
	private long sleepTime = DEFAULT_SLEEP_TIME;

	public void setTimes(int times) {
		this.times = times;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	/**
	 * @Description : 开始轮询操作
	 * @Method_Name : startPoll
	 * @return : void
	 * @Creation Date : 2017年7月11日 下午5:08:17
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	private void startPoll() {
		new Thread(() -> {
			while (1 > 0) {
				try {
//					JmsFailMsg failMsg = JedisUtils.rpop(FAIL_MQ_MESSAGE_POLL_POOL);
					JmsFailMsg failMsg = JedisClusterUtils.rpop(FAIL_MQ_MESSAGE_POLL_POOL);
					if (failMsg != null) {
						if (failMsg.getTimes() >= times) {
//							JedisUtils.lpush(FAIL_MQ_MESSAGE_MANUAL_POOL, failMsg);
							//持久化消费异常的jms消息
							this.recoverJmsFailMsgI.addJmsFailMsg(failMsg);
						} else {
							jmsService.sendMsgForPolling(
									JmsUtils.getDestination(failMsg.getDestinationType(), failMsg.getDestinationName()),
									failMsg.getJmsMessageType(), failMsg.getMsg(), failMsg.getDeliveryMode(),
									failMsg.getPriority(), failMsg.getTimeToLive(), failMsg.getTimes() + 1);
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.recoverJmsFailMsgI = JmsUtils.getRecoverJmsFailMsgI();
		//初始化消息发送失败次数
		String times = PropertiesHolder.getProperty("mq.fail.msg.send.times");
		if(StringUtils.isNotBlank(times)) {
			this.times = Integer.parseInt(times);
		}
		//初始化轮询间隔时间
		String sleepTime = PropertiesHolder.getProperty("mq.fail.msg.polling.time");
		if(StringUtils.isNotBlank(sleepTime)) {
			this.sleepTime = Long.parseLong(sleepTime);
		}
		startPoll();
	}
}
