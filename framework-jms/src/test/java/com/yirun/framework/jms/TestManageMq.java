package com.yirun.framework.jms;

import com.yirun.framework.jms.utils.TestJmsUtils;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;

/**
 * TODO
 *
 * @author zc.ding
 * @create 2018/5/4
 */
public class TestManageMq extends TestJmsUtils {
    
    @Test
    public void testGetQueues(){
        try {
            BrokerService broker = new BrokerService();
            //启用broker的JMX监控功能
            broker.setUseJmx(true);
            //设置broker名字
            broker.setBrokerName("MyBroker");
            //是否使用持久化
            broker.setPersistent(false);
            //添加连接协议，地址
            broker.addConnector("tcp://192.168.1.248:61616");
            broker.start();
            System.out.println(broker.getAdminView().getQueues());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
