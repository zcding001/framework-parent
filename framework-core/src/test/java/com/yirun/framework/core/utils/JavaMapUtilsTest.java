package com.yirun.framework.core.utils;

import java.util.Date;
import java.util.Map;

import com.yirun.framework.core.utils.JavaMapUtils;
/**
 * @Description   : JavaMapUtils 测试
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.JavaMapUtilsTest.java
 * @Author        : xuhuiliu@hongkun.com.cn 刘旭辉
 */
public class JavaMapUtilsTest {

    public static void main(String[] args) {
		A a = new A();
		a.setId(1);
		a.setName("aa");
		a.setCreatedTime(new Date());
		Map<String,String> result = null;
		try {
			result = JavaMapUtils.objectToMap(a);
			System.out.println(result.get("id"));
			System.out.println(result.get("createdTime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		A b = new A();
		try {
			b = (A) JavaMapUtils.mapToObject(result, A.class);
			System.out.println(b.getId());
			System.out.println(b.getCreatedTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    static class A{
    	private String name;
    	private int id;
    	private Date createdTime;
    	public Date getCreatedTime() {
			return createdTime;
		}
    	public void setCreatedTime(Date createdTime) {
			this.createdTime = createdTime;
		}
    	public int getId() {
			return id;
		}
    	public void setId(int id) {
			this.id = id;
		}
    	public String getName() {
			return name;
		}
    	public void setName(String name) {
			this.name = name;
		}
    }
}
