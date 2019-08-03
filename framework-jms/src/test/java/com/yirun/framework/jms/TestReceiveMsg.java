package com.yirun.framework.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;

public class TestReceiveMsg implements MessageListener {

    private Destination dest = null;  
    private Connection conn = null;  
    private Session session = null;  
    private MessageConsumer consumer = null;  
    private static Logger log = Logger.getLogger(TestReceiveMsg.class);
    
//    private static String URL = "tcp://127.0.0.1:61616";
    private static String URL = "tcp://47.93.205.153:61616";
    private static String USER_NAME = "admin";
    private static String PASSWORD = "admin";
	  
    private boolean stop = false;  
	  
    /**
     * 初始化  
     */
    private void initialize() throws JMSException, Exception {  
        // 连接工厂是用户创建连接的对象.  
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, URL);  
        connectionFactory.setTrustAllPackages(true);
        // 连接工厂创建一个jms connection  
        conn = connectionFactory.createConnection();
        //设置持久性订阅的clientId,注意设置的位置，确保在创建Session之前进行设置，否则出现异常
//        conn.setClientID("TEST-TOPIC-001");
        // 是生产和消费的一个单线程上下文。会话用于创建消息的生产者，消费者和消息。会话提供了一个事务性的上下文。  
        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE); // 不支持事务  
        // 目的地是客户用来指定他生产消息的目标还有他消费消息的来源的对象.  
//        dest = session.createQueue(Constants.QUEUE_NAME); 
//        dest = session.createQueue(Constants.DEST_NAME); 
        dest = session.createQueue("PRE_TEST"); 
//        dest = AdvisorySupport.getConnectionAdvisoryTopic();
//        dest = AdvisorySupport.getMessageDLQdAdvisoryTopic(new ActiveMQTempTopic("jms-queue"));
        // 会话创建消息的生产者将消息发送到目的地  
        consumer = session.createConsumer(dest);
//        String subName = "TEST-TOPIC-001-NAME";
//        dest = session.createTopic(Constants.TOPIC_NAME);
        //创建持久性订阅
//        consumer = session.createDurableSubscriber(session.createTopic(Constants.TOPIC_NAME), subName);
//        取消订阅
//        session.unsubscribe(subName);
    }  
  
    /** 
     * 消费消息 
     */  
    public void startReceiveMessage() throws JMSException, Exception {  
        initialize();  
        conn.start();  
        consumer.setMessageListener(this);  
        // 等待接收消息  
        while (!stop) {  
            Thread.sleep(5000);  
        }  
    }  
  
    @Override  
    public void onMessage(Message msg) {  
    	log.info(msg);
//    	ObjectMessage om = (ObjectMessage) msg;
//        try {
//			if (om.getObject() instanceof RequestJmsModel) {  
//				RequestJmsModel message = (RequestJmsModel) (om.getObject());  
//			    System.out.println("------Received RequestJmsModel------");  
//			    System.out.println(message.getRequestJson());  
//			} else {  
//			    System.out.println(msg);  
//			}
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}  
        stop = true;  
    }  
  
    /** 
     * 关闭连接  
     */  
    public void close() throws JMSException {  
        System.out.println("Consumer:->Closing connection");  
        if (consumer != null)  
            consumer.close();  
        if (session != null)  
            session.close();  
        if (conn != null)  
            conn.close();  
    }  
    
//    @Test
//	public void testReceiveMsg() throws Exception {
//    	startReceiveMessage();
//	}
    
    public static void main(String[] args) {
		try {
			new TestReceiveMsg().startReceiveMessage();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
