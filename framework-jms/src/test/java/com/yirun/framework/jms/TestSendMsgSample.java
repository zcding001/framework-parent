package com.yirun.framework.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.*;

/**
 * TODO
 *
 * @author zc.ding
 * @create 2018/6/4
 */
public class TestSendMsgSample {

    private Destination dest = null;
    private Connection conn = null;
    private Session session = null;
    private MessageConsumer consumer = null;
    private static Logger log = Logger.getLogger(TestReceiveMsg.class);

    //    private static String URL = "tcp://127.0.0.1:61616";
    private static String URL = "tcp://47.93.205.153:61616";
    private static String USER_NAME = "admin";
    private static String PASSWORD = "admin";
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
       // 是生产和消费的一个单线程上下文。会话用于创建消息的生产者，消费者和消息。会话提供了一个事务性的上下文。  
        session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE); // 不支持事务  
        // 目的地是客户用来指定他生产消息的目标还有他消费消息的来源的对象.  
//        dest = session.createQueue(Constants.QUEUE_NAME); 
//        dest = session.createQueue(Constants.DEST_NAME); 
        dest = session.createQueue("PRE_TEST");
        // 会话创建消息的生产者将消息发送到目的地  
        consumer = session.createConsumer(dest);

        Destination destination = new ActiveMQQueue("PRE_TEST");

        MessageProducer messageProducer = session.createProducer(destination);

        String object = "你好";
        Message message = session.createTextMessage((String)object);;
        message.setJMSDestination(destination);
        message.setJMSDeliveryMode(Message.DEFAULT_DELIVERY_MODE);
        message.setJMSPriority(Message.DEFAULT_PRIORITY);
        message.setJMSTimestamp(Message.DEFAULT_TIME_TO_LIVE);
        messageProducer.send(message);
    }

    public static void main(String[] args) throws Exception {
        TestSendMsgSample tsm = new TestSendMsgSample();
        tsm.initialize();
    }
}
