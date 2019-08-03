//package com.yirun.framework.jms;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.jms.Message;
//
//import org.junit.Test;
//
//import com.yirun.framework.core.utils.DateUtils;
//import com.yirun.framework.core.utils.json.JsonUtils;
//import com.yirun.framework.jms.commons.Constants;
//import com.yirun.framework.jms.destination.CustActiveMQQueue;
//import com.yirun.framework.jms.destination.CustActiveMQTopic;
//import com.yirun.framework.jms.enums.DestinationType;
//import com.yirun.framework.jms.enums.JmsMessageType;
//import com.yirun.framework.jms.model.RegUser;
//import com.yirun.framework.jms.model.RequestJmsModel;
//import com.yirun.framework.jms.utils.TestSendJmsUtils;
//
//public class TestSendMsg extends TestSendJmsUtils {
//	
//	private static String DEST_NAME = Constants.DEST_NAME;
//	
//	/**
//	 * @Described			: 发送文本消息到QUEUE
//	 * @author				: zc.ding
//	 * @project				: framework-jms
//	 * @package				: com.yirun.framework.jms.TestSendMsg.java
//	 * @return				: void
//	 * @date 				: 2017年2月21日
//	 * @throws Exception
//	 */
//	@Test
//	public void testSendTextMsg() throws Exception {
//		String textMsg = "你好，世界!";
//		String[] arr = new String[]{"BUS-TEST1"};
////		方式一
////		jmsService.sendTextMsg(
////				Constants.DEST_NAME, 
////				textMsg, DestinationType.QUEUE, 
////				DeliveryMode.PERSISTENT,
////				Constants.PRIORITY_4, 
////				Message.DEFAULT_TIME_TO_LIVE
////				);
////		方式二
//		for(int i = 0; i < arr.length; i++){a
//			String dName = arr[i];
//			jmsService.sendMsg(
//					dName, 
//					DestinationType.QUEUE, 
//					textMsg + i, 
//					JmsMessageType.TEXT
//					);
//		}
//	}
//	
//	/**
//	 * @Described			: 发送文本消息到TOPIC
//	 * @author				: zc.ding
//	 * @project				: framework-jms
//	 * @package				: com.yirun.framework.jms.TestSendMsg.java
//	 * @return				: void
//	 * @date 				: 2017年2月22日
//	 * @throws Exception
//	 */
//	@Test
//	public void testSendTextMsgTopic() throws Exception {
//		String textMsg = "你好，世界!";
//		String[] arr = new String[]{"BUS-TEST4"};
////		方式一
////		jmsService.sendTextMsg(
////				Constants.TOPIC_NAME, 
////				textMsg, 
////				DestinationType.TOPIC, 
////				DeliveryMode.PERSISTENT, 
////				Constants.PRIORITY_4, 
////				Message.DEFAULT_TIME_TO_LIVE
////				);
////		方式二
//		for(int i = 0; i < arr.length; i++){
//			String dName = arr[i];
//			jmsService.sendMsg(
//					dName, 
//					DestinationType.TOPIC, 
//					textMsg + i, 
//					JmsMessageType.TEXT);
//		}
//	}
//	
//	/**
//	 * @Described			: jms发送对象消息到QUEUE
//	 * @author				: zc.ding
//	 * @project				: framework-jms
//	 * @package				: com.yirun.framework.jms.TestSendMsg.java
//	 * @return				: void
//	 * @date 				: 2017年2月21日
//	 * @throws Exception
//	 */
//	@Test
//	public void testSendObjectMsg() throws Exception {
//		RequestJmsModel jmsModel = getJmsModel();
////		方式一
////		jmsService.sendObjectMsg(
////				Constants.QUEUE_NAME, 
////				jmsModel, 
////				DestinationType.QUEUE, 
////				DeliveryMode.PERSISTENT,
////				Constants.PRIORITY_4, 
////				Message.DEFAULT_TIME_TO_LIVE
////				);
////		方式二
//		jmsService.sendMsg(
//				DEST_NAME, 
//				DestinationType.QUEUE, 
//				jmsModel, 
//				JmsMessageType.OBJECT
//				);
//	}
//	
//	/**
//	 * @Described			: jms发送对象消息到TOPIC
//	 * @author				: zc.ding
//	 * @project				: framework-jms
//	 * @package				: com.yirun.framework.jms.TestSendMsg.java
//	 * @return				: void
//	 * @date 				: 2017年2月21日
//	 * @throws Exception
//	 */
//	@Test
//	public void testSendObjectMsgToTopic() throws Exception {
//		RequestJmsModel jmsModel = getJmsModel();
//		
////		方式一
////		jmsService.sendObjectMsg(
////				Constants.TOPIC_NAME, 
////				jmsModel, 
////				DestinationType.TOPIC, 
////				DeliveryMode.PERSISTENT,
////				Constants.PRIORITY_4, 
////				Message.DEFAULT_TIME_TO_LIVE
////				);
////		方式二
//		jmsService.sendMsg(
//				DEST_NAME, 
//				DestinationType.TOPIC, 
//				jmsModel, 
//				JmsMessageType.OBJECT
//				);
//		
//	}
//	
//	/**
//	 * @Described			: 发送Map消息
//	 * @author				: zc.ding
//	 * @project				: framework-jms
//	 * @package				: com.yirun.framework.jms.TestSendMsg.java
//	 * @return				: void
//	 * @date 				: 2017年2月22日
//	 * @throws Exception
//	 */
//	@Test
//	public void testSendMapMsg() throws Exception{
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("a", "hello");
//		map.put("b", "world");
////		发送消息到队列中
//		jmsService.sendMsg(
//				DEST_NAME, 
//				DestinationType.QUEUE, 
//				map, 
//				JmsMessageType.MAP
//				);
////		发送消息到TOPIC中
////		jmsService.sendMsg(
////				DEST_NAME, 
////				DestinationType.TOPIC, 
////				map, 
////				JmsMessageType.MAP
////				);
//	}
//	
//	/**
//	 * @Described			: 将同一个消息发送到多个queue目的地
//	 * @author				: zc.ding
//	 * @project				: framework-jms
//	 * @package				: com.yirun.framework.jms.TestSendMsg.java
//	 * @return				: void
//	 * @date 				: 2017年3月2日
//	 * @throws Exception
//	 */
//	@Test
//	public void testSendCompositeQueues() throws Exception{
//		String textMsg = "hello，world!";
////		ActiveMQQueues destination = new ActiveMQQueues();
////		destination.setDesitions("YRTZ-TEST, YRTZ-TEST1, YRTZ-TEST2");
//		String destNames = "YRTZ-TEST, YRTZ-TEST1, YRTZ-TEST2";
//		CustActiveMQQueue destination = new CustActiveMQQueue();
//		destination.setDesitions(destNames);
//		jmsService.sendMsg(
//				destination, 
//				textMsg, 
//				Message.DEFAULT_DELIVERY_MODE, 
//				Message.DEFAULT_PRIORITY, 
//				Message.DEFAULT_TIME_TO_LIVE
//				);
//	}
//	
//	/**
//	 * @Described			: 将同一信息发送到多个topic目的地址
//	 * @author				: zc.ding
//	 * @project				: framework-jms
//	 * @package				: com.yirun.framework.jms.TestSendMsg.java
//	 * @return				: void
//	 * @date 				: 2017年3月2日
//	 * @throws Exception
//	 */
//	@Test
//	public void testSendCompositeTopics() throws Exception{
//		String textMsg = "hello, world!";
//		String destNames = "YRTZ-TEST, YRTZ-TEST1, YRTZ-TEST2";
//		CustActiveMQTopic destination = new CustActiveMQTopic();
//		destination.setDesitions(destNames);
//		jmsService.sendMsg(
//				destination, 
//				textMsg, 
//				Message.DEFAULT_DELIVERY_MODE, 
//				Message.DEFAULT_PRIORITY, 
//				Message.DEFAULT_TIME_TO_LIVE
//				);
//	}
//	
//	
//	/**
//	 * @Described			: 创建jms对象数据
//	 * @author				: zc.ding
//	 * @project				: framework-jms
//	 * @package				: com.yirun.framework.jms.TestSendMsg.java
//	 * @return				: RequestJmsModel
//	 * @date 				: 2017年2月21日
//	 * @return
//	 */
//	RequestJmsModel getJmsModel(){
//		RegUser regUser = new RegUser();
//		regUser.setId(1);
//		regUser.setUsername("peter.zhou");
//		regUser.setPassword("admin");
//		regUser.setCreateDate(DateUtils.getCurrentDate());
//		
//		RequestJmsModel jmsModel = new RequestJmsModel();
//		jmsModel.setRequestJson(JsonUtils.toJson(regUser));
//		jmsModel.setRequestClazzType(RegUser.class);
//		return jmsModel;
//	}
//}
