package com.yirun.framework.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description : TODO
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.redis
 * @Author : xuhuiliu@hongkun.com.cn 劉旭輝
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-redis.xml"})
public class TestJedisClusterUtils {

    @Test
    public void  basictest() {

//        System.out.print(JedisClusterUtils.exists("aaaa"));
        JedisClusterUtils.set("ccb","ccc");
        String ccc = JedisClusterUtils.get("ccb");
        System.out.print(ccc);
// ;
//        List<String> list = new ArrayList<String>();
//        list.add("a");
//        list.add("b");
//        list.add("c");
//        JedisClusterUtils.setList("listTest", list);
//        List<String> resultList = JedisClusterUtils.getList("listTest", 1, 2);
//        resultList.forEach(s->{
//            System.out.print(s);
//        });
//        JedisClusterUtils.setExpireTime("a",30);
//        JedisClusterUtils.deleteKeysByPrefix("z");
//        JedisClusterUtils.set("y1".getBytes(),ObjectUtilsExtend.objectToBytes("1233"));
//        JedisClusterUtils.set("q2".getBytes(),"1234".getBytes());

//        System.out.print(JedisClusterUtils.get("lxh".getBytes()));
//        System.out.print(JedisClusterUtils.get("lxh"));
//        ;
//          List<String> s = JedisClusterUtils.get("y*",String.class);
//        System.out.print(s.size());
//        System.out.print("lxh".getBytes());
//        JedisClusterUtils.delete("lisi");
//        oo.setAge(1);
//        oo.setName("lisi");
//        JedisClusterUtils.setAsJson("lisi",oo);
//        O1 result = JedisClusterUtils.getObjectForJson("lisi",O1.class);
//        System.out.print(result.getAge());
    }
    @Test
    public void t11(){
        System.out.print(Math.round(Math.random()*1000));
    }

    @Test
    public void getLock(){
        JedisClusterLock lock = new JedisClusterLock();
        System.out.print("================"+lock.lock("lock_test"));
    }
    
    @Test
    public void testScan(){
        JedisCluster jedisCluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
        System.out.println(jedisCluster.get("aa"));
        String cursor = "0";
        List<String> allKeys = new ArrayList<String>();
        String pattern = "{RegUser58}";
        do {
            ScanResult<String> scanResult = jedisCluster.scan(cursor, new ScanParams().match(pattern).count(30));
            allKeys.addAll(scanResult.getResult());
            cursor = scanResult.getStringCursor();
        } while (!cursor.equals("0") || allKeys.size() >= 50);
        System.out.println(allKeys);
        System.out.println(jedisCluster.get("RegUser58"));
    }
    
    @Test
    public void testGetMaster(){
        JedisCluster jedisCluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
        Map<String, JedisPool> map = jedisCluster.getClusterNodes();
        for(String idx : map.keySet()){
            JedisPool pool = map.get(idx);
            Jedis jedis = pool.getResource();
            List<String> list = jedis.clusterSlaves(idx);
            System.out.println(list);
//            System.out.println(pool.getResource().clusterNodes());
//            System.out.println(pool.getResource().clusterInfo());
        }
    }


    @Test
    public void testBatchGet() {
        RedisClusterPipeline pipeline = RedisClusterPipeline.pipeline(JedisClusterUtils.getJedisCluster());

        long startTime = System.currentTimeMillis();
        Map<String, Response<String>> result = new HashMap<>();
        for (int i = 2000; i <= 3000   ; i++) {
            String key = "RegUser" + i;
            result.put(key, pipeline.get(key));
        }
        pipeline.sync();
        for (String key : result.keySet()) {
            System.out.println(result.get(key).get());
        }
        pipeline.close();
        System.out.println("time: " + (System.currentTimeMillis() - startTime));
    }


    @Test
    public void testJedisClusterGet() {
        long startTime = System.currentTimeMillis();
        Map<String, String> result = new HashMap<>();
        for (int i = 2000; i <= 3000   ; i++) {
            String key = "RegUser" + i;
            result.put(key, JedisClusterUtils.get(key));
        }
        for (String key : result.keySet()) {
            System.out.println(result.get(key));
        }
        System.out.println("time: " + (System.currentTimeMillis() - startTime));
    }
}

class O1 {
    private int age ;
    private String name ;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}