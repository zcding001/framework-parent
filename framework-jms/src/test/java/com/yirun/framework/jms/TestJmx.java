package com.yirun.framework.jms;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.Test;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.*;

/**
 * jmx操作activemq队列
 *
 * @author zc.ding
 * @create 2018/11/12
 */
public class TestJmx {
    
    @Test
    public void delQueue() throws Exception{
        Map<String, String[]> env = new HashMap<>();
        String url = "service:jmx:rmi:///jndi/rmi://192.168.1.248:1616/jmxrmi";
        String[] credentials = new String[] {"admin", "activemq"};
        env.put(JMXConnector.CREDENTIALS, credentials);
        JMXServiceURL urls = new JMXServiceURL(url);
        JMXConnector connector = JMXConnectorFactory.connect(urls,env);
        connector.connect();
        MBeanServerConnection conn = connector.getMBeanServerConnection();
        //这里brokerName的b要小写，大写会报错
        ObjectName name = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
        BrokerViewMBean mBean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(conn, name, BrokerViewMBean.class, true);
        //获取点对点的队列mBean.getTopics() 获取订阅模式的队列
        List<String> delList = new ArrayList<>();
        for(ObjectName na : mBean.getQueues()){
            QueueViewMBean queueBean = (QueueViewMBean)
                    MBeanServerInvocationHandler.newProxyInstance(conn, na, QueueViewMBean.class, true);
            System.out.println("******************************");
            System.out.println("队列的名称："+queueBean.getName());
            System.out.println("\t队列中消息数："+queueBean.getQueueSize());
            System.out.println("\t消费者数："+queueBean.getConsumerCount());
            System.out.println("\t出队列的数量："+queueBean.getDequeueCount());
            //删除没有消费者的队列
            if(queueBean.getConsumerCount() == 0){
                System.out.println("删除队列：" + queueBean.getName());
                mBean.removeQueue(queueBean.getName());
            }
        }
    }
}
