package com.yirun.framework.core.utils;

import java.text.DecimalFormat;

/**
 * @Description   : 数据格式化工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.DecimalFormatUtils.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class DecimalFormatUtils {

	private static final String OUT_FORMAT = "###########0.0#####";

	/**
	 * @Description:格式化long数据
	 */
	public static String format(long number) {
		DecimalFormat format = new DecimalFormat(OUT_FORMAT);
		return format.format(number);
	}

	/**
	 * @Description:格式化float数据
	 */
	public static String format(float number) {
		DecimalFormat format = new DecimalFormat(OUT_FORMAT);
		return format.format(number);
	}

	/**
	 * @Description:格式化double数据
	 */
	public static String format(double number) {
		DecimalFormat format = new DecimalFormat(OUT_FORMAT);
		return format.format(number);
	}
}
