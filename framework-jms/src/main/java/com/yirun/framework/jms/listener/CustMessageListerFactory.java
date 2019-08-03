package com.yirun.framework.jms.listener;

import java.util.concurrent.TimeUnit;

import javax.jms.Message;
import javax.jms.MessageListener;

import com.yirun.framework.jms.JmsMessageHandler;

/**
 * @Described	: jms消息监听器创建工厂
 * @project		: com.yirun.framework.jms.listener.CustMessageListerFactory
 * @author 		: zc.ding
 * @date 		: 2017年3月4日
 */
public class CustMessageListerFactory {
	
	public static MessageListener createListener(JmsMessageHandler jmsMessageHandler){
		return new MessageListener() {
			@Override
			public void onMessage(Message message) {
				try {
					jmsMessageHandler.handlerMessage(message);
					// sleep
					if (jmsMessageHandler.getTimeInterval() > 0) {
						TimeUnit.MILLISECONDS.sleep(jmsMessageHandler.getTimeInterval());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
}
