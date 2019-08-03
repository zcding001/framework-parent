//package com.yirun.framework.redis;
//
//import com.yirun.framework.core.utils.json.JsonUtils;
//import org.apache.log4j.Logger;
//import org.codehaus.jackson.type.TypeReference;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import redis.clients.jedis.JedisCluster;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:/applicationContext-redis.xml"})
//public class TestJedisUtils {
//	private static final Logger logger = Logger.getLogger(TestJedisUtils.class);
////	public static void main(String[] args) {
////		System.out.println(RedisSinglePool.class.getResource("/").getPath());
////	}
//	/**
//	 *
//	  *  @Description    : 普通方法测试，测试redis数据类型插入和读取
//	  *  @Method_Name    : testBasicM
//	  *  @param
//	  *  @return         : void
//	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
//	  *  @date			  ：2017年3月13日
//	 */
//	@Test
//	public  void testBasicM(){
//		//test1    set/get String
//		JedisUtils.set("str", "success");
//		String str = JedisUtils.get("str");
//		Assert.assertEquals("success", str);
//		//test2  setAsJson
//		TestBean t = new TestBean();
//		t.setId(1);
//		t.setName("xuhui");
//		JedisUtils.setAsJson("jsonStr",t,0);
//		TestBean tResult = JedisUtils.getObjectForJson("jsonStr",TestBean.class);
//		Assert.assertEquals(1, tResult.getId());
//		//delete/exists
//		JedisUtils.delete("str");
//		boolean existFlag = JedisUtils.exists("str");
//		Assert.assertEquals(false, existFlag);
//
//	}
//	@Test
//	public void testList(){
//		List<String> list = new ArrayList<String>();
//		list.add("a");
//		list.add("b");
//		list.add("c");
//		JedisUtils.setList("mylist", list);
//		List<String> result = JedisUtils.getList("mylist", 0, -1);
//		System.out.println(result.size());
//	}
//	/**
//	  *  @Description    : 通过json格式存储list
//	  *  @Method_Name    : testListT
//	  *  @param
//	  *  @return         : void
//	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
//	  *  @date			  ：2017年5月24日
//	 */
//	@Test
//	public void testListT(){
//		List<TestBean> list = new ArrayList<TestBean>();
//		TestBean testBean1 = new TestBean();
//		testBean1.setId(1);
//		TestBean testBean2 = new TestBean();
//		testBean2.setId(2);
//		TestBean testBean3 = new TestBean();
//		testBean3.setId(3);
//		list.add(testBean1);
//		list.add(testBean2);
//		list.add(testBean3);
//		JedisUtils.setAsJson("listT", list, 0);
//
//
//		JedisUtils.setAsByte("byteList", list);
//		List<TestBean> ll =  JedisUtils.getObjectForByte("byteList");
//		for(TestBean t :ll){
//			System.out.println(t.getId());
//		}
////		System.out.println(ll.size());
//	}
//	@Test
//	public void testLock(){
//		JedisLock jl = new JedisLock();
//		boolean result = jl.lock("bidd_code_test", 10,10);
//
//		Assert.assertEquals(true, result);
//		boolean free = jl.freeLock("bidd_code_test");
//		Assert.assertEquals(true, free);
//	}
//
//
//
//	/**
//	  *  @Description    : 测试jedis链接是否释放
//	  *  @Method_Name    : testFreeConnections
//	  *  @param
//	  *  @return         : void
//	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
//	  *  @date			  ：2017年3月13日
//	 */
//	@Test
//	public  void  testFreeConnections(){
//		int connNum1 = RedisSinglePool.getInstance().userConnections();
//		int idlNum1 = RedisSinglePool.getInstance().getNumIdle();
//
//		System.out.println("START 连接数："+connNum1+",空闲连接数："+idlNum1);
//
//		JedisUtils.set("connTest", "success");
//		int connNum2 = RedisSinglePool.getInstance().userConnections();
//		int idlNum2 = RedisSinglePool.getInstance().getNumIdle();
//		System.out.println("SECOND 连接数："+connNum2+",空闲连接数："+idlNum2);
//
//		int connNum3 = RedisSinglePool.getInstance().userConnections();
//		int idlNum3 = RedisSinglePool.getInstance().getNumIdle();
//		System.out.println("THIRD 连接数："+connNum3+",空闲连接数："+idlNum3);
//
//		JedisUtils.delete("connTest");
//		int connNum4 = RedisSinglePool.getInstance().userConnections();
//		int idlNum4 = RedisSinglePool.getInstance().getNumIdle();
//		System.out.println("FOURTH 连接数："+connNum4+",空闲连接数："+idlNum4);
//	}
//
//	/**
//	  *  @Description    : 多线程测试链接回收
//	  *  @Method_Name    : testThreadMoreForUtil
//	  *  @param
//	  *  @return         : void
//	  *  @Author         : xuhuiliu@hongkun.com.cn 刘旭辉
//	  *  @date			  ：2017年3月20日
//	 */
//	@Test
//	public void testThreadMoreForUtil(){
//		for(int i=0;i<=100;i++){
//			Thread t = new Thread(){
//				@Override
//				public void run(){
//					System.out.println("thread=========================="+RedisSinglePool.getInstance().getPool().toString());
//					int num = RedisSinglePool.getInstance().userConnections();
//					int idl = RedisSinglePool.getInstance().getNumIdle();
//					JedisUtils.get("aaa");
//					int num2 = RedisSinglePool.getInstance().userConnections();
//					int idl2 = RedisSinglePool.getInstance().getNumIdle();
//					System.out.println("thread1:"+num+","+idl);
//					System.out.println("thread1:"+num2+","+idl2);
//				}
//			};
//			t.start();
//		}
//	}
//
//	@Test
//	public  void testHash(){
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("name", "zhangsan");
//		JedisUtils.setMap("hash_map", map);
//		String name = JedisUtils.getHash("hash_map", "name");
//		Assert.assertEquals("zhangsan", name);
//
//		TestBean t = new TestBean();
//		t.setId(1);
//		t.setName(name);
//
//		JedisUtils.setAsMap("has_map", t, 0);
//		TestBean b = JedisUtils.getObjectForMap("has_map",TestBean.class);
//		Assert.assertEquals(1, b.getId());
//	}
//
//	@Test
//	public void testPush(){
////		JedisUtils.lpush("qq", "c1");
////		JedisUtils.lpush("qq", "c2");
////		String s = JedisUtils.rpop("qq");
////		System.out.println(s);
//		List<String> ss =  (List<String>) JedisUtils.pullList("hh");
//		System.out.println(ss.size());
////		for(String aaa:ss){
////			System.out.println(aaa);
////		}
//	}
//	@Test
//	public void testJson(){
//		List<TestBean> s = new ArrayList<TestBean>();
//		TestBean t1 = new TestBean();
//		t1.setId(1);
//		TestBean t2 = new TestBean();
//		t2.setId(2);
//		s.add(t1);
//		s.add(t2);
//		JedisUtils.setAsJson("ccc", s);
//		List<TestBean> ss = new ArrayList<TestBean>();
//		ss = (List<TestBean>) JedisUtils.getObjectForJson("ccc", new TypeReference< List<TestBean> >(){});
//		for(TestBean str:ss){
//			System.out.println(str.getId());
//		}
//	}
//
//	/**
//	 *  @Description    : 以json存储 嵌套object 测试
//	 *  @Method_Name    : testJson2
//	 *  @return         : void
//	 *  @Creation Date  : 2017年11月8日 上午10:03:40
//	 *  @Author         : xuhuiliu@hongkun.com.cn 劉旭輝
//	 */
//	@Test
//	public void testJson2(){
//		TestBean t1 = new TestBean();
//		t1.setId(1);
//		t1.setName("my name is t1");
//		TestBean2 t2 = new TestBean2();
//		t2.setId(2);
//		t2.setName("my name is t2");
//		t1.setTestBean2(t2);
//		JedisUtils.setAsJson("ccc", t1);
//		List<TestBean> list1 = new ArrayList<TestBean>();
//		list1.add(t1);
//		String jsonStr = JsonUtils.toJson(list1);
//		JedisUtils.setAsJson("ccc",t1);
////		JsonUtils.
////		List<TestBean> result =  JsonUtils.json2GenericObject(jsonStr, new TypeReference<List<TestBean>>() {},null);
////		System.out.print(result.get(0).getId());
//		TestBean result = JedisUtils.getObjectForJson("ccc", TestBean.class);
////		System.out.println(result.getTestBean2().getName());
//	}
//
//
//	@Test
//	public void testKeys(){
//		JedisUtils.set("aa", "kk");
//		JedisUtils.set("ab", "kk");
//		JedisUtils.set("ac", "kk");
//		JedisUtils.set("ad", "kk");
//		System.out.println(JedisUtils.deleteKeysByPrefix("a"));
//	}
//
//	@Test
//	public void testDel(){
////	    JedisUtils.deleteKeysByPrefix("findVasRebatesRule_VAS_");
////	    System.out.println("okok");
//	    List<String> s =  JedisUtils.get("*",String.class);
//	    System.out.print(s.size());
//    }
//
//	@Test
//    public void testCluster(){
//		JedisCluster cluster = RedisSinglePoolCluster.getInstance().getJedisCluster();
//		cluster.set("aaaa","111");
//		System.out.print(cluster.get("aaaa"));
//		try {
//			cluster.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//}
//
//
//
//class TestBean implements Serializable{
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 1L;
//	private int id;
//	private String name;
//	private TestBean2 testBean2;
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public TestBean2 getTestBean2() {
//		return testBean2;
//	}
//	public void setTestBean2(TestBean2 testBean2) {
//		this.testBean2 = testBean2;
//	}
//}
//
//
//class TestBean2 implements Serializable{
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 1L;
//	private int id;
//	private String name;
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//}
//
