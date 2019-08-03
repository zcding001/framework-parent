
/**
 * 
 */
package com.yirun.framework.core.enums;

/**
 * @Description : caoxb 类描述 平台来源常量
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.enums.PlatformSourceEnums.java
 * @Author : caoxinbang@hongkun.com.cn 曹新帮
 */

public enum PlatformSourceEnums {

	PC("PC", 10), IOS("IOS", 11), ANDROID("ANDROID", 12), WAP("WAP", 13), WX("WX", 14);

	/**
	 * 标识
	 */
	private String type;
	/**
	 * 名称
	 */
	private int value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	private PlatformSourceEnums(String type, int value) {

		this.type = type;
		this.value = value;

	}

	/**
	 * 
	 * @Description : 获得平台来源
	 * @Method_Name : nameByType
	 * @param type
	 * @return
	 * @return : String
	 * @Creation Date : 2017年6月7日 下午2:07:36
	 * @Author : caoxinbang@hongkun.com.cn 曹新帮
	 */
	public static int valueByType(String type) {
		for (PlatformSourceEnums s : PlatformSourceEnums.values()) {
			if (s.getType().equals(type)) {
				return s.getValue();
			}
		}
		return -1;
	}

	/**
	 * @Description : 根据平台类型值，获取平台类型
	 * @Method_Name : typeByValue;
	 * @param value
	 * @return
	 * @return : String;
	 * @Creation Date : 2018年3月12日 下午7:24:18;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public static String platFormTypeByValue(Integer value) {
		for (PlatformSourceEnums s : PlatformSourceEnums.values()) {
			if (s.getValue() == value) {
				return s.getType();
			}
		}
		return null;
	}

	/**
	 * @Description :通过平台的值获取平台枚举
	 * @Method_Name : typeByValue;
	 * @param value
	 *            平台来源VALUE
	 * @return
	 * @return : PlatformSourceEnums;
	 * @Creation Date : 2017年11月20日 下午3:37:23;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public static PlatformSourceEnums typeByValue(int value) {
		for (PlatformSourceEnums s : PlatformSourceEnums.values()) {
			if (s.getValue() == value) {
				return s;
			}
		}
		return null;
	}

	/**
	 * @Description : 通过平台的类型获取平台枚举
	 * @Method_Name : valueByType;
	 * @param type
	 * @return
	 * @return : PlatformSourceEnums;
	 * @Creation Date : 2017年11月20日 下午4:47:57;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public static PlatformSourceEnums platformTypeByType(String type) {
		for (PlatformSourceEnums s : PlatformSourceEnums.values()) {
			if (s.getType().equals(type)) {
				return s;
			}
		}
		return null;
	}
}
