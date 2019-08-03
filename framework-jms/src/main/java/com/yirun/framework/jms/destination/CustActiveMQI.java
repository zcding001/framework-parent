package com.yirun.framework.jms.destination;

import org.apache.activemq.command.ActiveMQDestination;

@FunctionalInterface
public interface CustActiveMQI {

	/**
	 * @Described			: 创建指定name的Desination
	 * @author				: zc.ding
	 * @project				: framework-jms
	 * @package				: com.yirun.framework.jms.destination.QkdActiveMQInitI.java
	 * @return				: ActiveMQDestination
	 * @date 				: 2017年3月4日
	 * @param name
	 * @return
	 */
	ActiveMQDestination getDestination(String name);
}
