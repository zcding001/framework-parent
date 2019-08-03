package com.yirun.framework.core.utils;

import java.util.Date;

public class DateUtilsTest {
	public static final String DATE_HH_MM = "yyyy-MM-dd HH:mm";
	 public static void main(String[] args) {
			String start = "2017-06-13 08:00:00";
			String end = "2017-06-14 09:01:00";
			Date d1 = DateUtils.parse(start,DATE_HH_MM);
			Date d2 = DateUtils.parse(end,DATE_HH_MM);
			System.out.println(DateUtils.getDaysBetween(d1, d2));
		}
}
