
/**
 * 
 */
package com.yirun.framework.core.enums;

/**
 * @Description : caoxb 类描述:系统类型
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.enums.SystemTypeEnums.java
 * @Author : caoxinbang@hongkun.com.cn 曹新帮
 */

public enum SystemTypeEnums {

	QKD("QKD", 1), QSH("QSH", 2), HKJF("HKJF", 3), CXVIP("CXVIP", 4);

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

	private SystemTypeEnums(String type, int value) {

		this.type = type;
		this.value = value;

	}

	/**
	 * 
	 * @Description : 获得平台值
	 * @Method_Name : valueByType
	 * @param type
	 * @return
	 * @return : String
	 * @Creation Date : 2017年6月7日 下午2:07:36
	 * @Author : caoxinbang@hongkun.com.cn 曹新帮
	 */
	public static int valueByType(String type) {
		for (SystemTypeEnums s : SystemTypeEnums.values()) {
			if (s.getType().equals(type)) {
				return s.getValue();
			}
		}
		return -1;
	}

	/**
	 * @Description :通过系统VALUE获取系统类型
	 * @Method_Name : sysTypeByValue;
	 * @param value
	 *            系统值
	 * @return
	 * @return : String;
	 * @Creation Date : 2018年5月11日 下午2:47:07;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public static String sysTypeByValue(int value) {
		for (SystemTypeEnums systemTypeEnums : SystemTypeEnums.values()) {
			if (systemTypeEnums.getValue() == value) {
				return systemTypeEnums.getType();
			}
		}
		return "";
	}

	/**
	 * @Description : 通过系统类型VALUE值，获取枚举类型
	 * @Method_Name : getSystemTypeEnumsByValue;
	 * @param value
	 * @return
	 * @return : SystemTypeEnums;
	 * @Creation Date : 2018年5月14日 上午9:22:24;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public static SystemTypeEnums getSystemTypeEnumsByValue(int value) {
		for (SystemTypeEnums systemTypeEnums : SystemTypeEnums.values()) {
			if (systemTypeEnums.getValue() == value) {
				return systemTypeEnums;
			}
		}
		return null;
	}
}
