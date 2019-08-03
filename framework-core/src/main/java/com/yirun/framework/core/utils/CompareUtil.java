package com.yirun.framework.core.utils;

import java.math.BigDecimal;

/**
 * 数字比较器工具类
 * @author 	 zc.ding
 * @since 	 2017年5月2日
 * @version  1.1
 */
public class CompareUtil {

	private CompareUtil(){}
	
	/**
	 *  @Description    : num > 0
	 *  @Method_Name    : gtZero
	 *  @param num
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午4:59:10 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean gtZero(BigDecimal num){
		return num != null && num.compareTo(BigDecimal.ZERO) > 0;
	}
	
	/**
	 *  @Description    : num >= 0
	 *  @Method_Name    : gteZero
	 *  @param num
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午4:59:25 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean gteZero(BigDecimal num){
		return num != null && num.compareTo(BigDecimal.ZERO) >= 0;
	}
	
	/**
	 *  @Description    : num < 0
	 *  @Method_Name    : ltZero
	 *  @param num
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午4:59:35 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean ltZero(BigDecimal num){
		return num != null && num.compareTo(BigDecimal.ZERO) < 0;
	}
	
	/**
	 *  @Description    : num <= 0
	 *  @Method_Name    : lteZero
	 *  @param num
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午4:59:44 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean lteZero(BigDecimal num){
		return num != null && num.compareTo(BigDecimal.ZERO) <= 0;
	}
	
	/**
	 *  @Description    : num = 0
	 *  @Method_Name    : eZero
	 *  @param num
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午4:59:52 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean eZero(BigDecimal num){
		return num != null && num.compareTo(BigDecimal.ZERO) <= 0;
	}
	
	/**
	 *  @Description    : 比较first 是否与 second 相等
	 *  @Method_Name    : eq
	 *  @param first
	 *  @param second
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午5:24:57 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean eq(BigDecimal first, double second){
		return first != null && first.compareTo(BigDecimal.valueOf(second)) == 0;
 	}
	
	/**
	 *  @Description    : 比较first 是否与 second 相等
	 *  @Method_Name    : eq
	 *  @param first
	 *  @param second
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午5:24:42 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean eq(BigDecimal first, BigDecimal second){
		return first != null && second != null && first.compareTo(second) == 0;
	}
	
	/**
	 *  @Description    : first > second
	 *  @Method_Name    : gt
	 *  @param first
	 *  @param second
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午5:00:00 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean gt(BigDecimal first, BigDecimal second){
		return first != null && second != null && first.compareTo(second) > 0;
	}
	
	/**
	 *  @Description    : first > second
	 *  @Method_Name    : gt
	 *  @param first
	 *  @param second
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午6:33:27 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean gt(BigDecimal first, double second){
		return first != null && first.compareTo(BigDecimal.valueOf(second)) > 0;
	}
	
	/**
	 *  @Description    : first > second
	 *  @Method_Name    : gt
	 *  @param first
	 *  @param second
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月14日 上午9:48:17 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean gt(double first, BigDecimal second){
		return second != null && BigDecimal.valueOf(first).compareTo(second) > 0;
	}
	
	/**
	 *  @Description    : first >= second
	 *  @Method_Name    : gte
	 *  @param first
	 *  @param second
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午5:00:09 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean gte(BigDecimal first, BigDecimal second){
		return first != null && second != null && first.compareTo(second) >= 0;
	}
	
	/**
	 *  @Description    : first >= second
	 *  @Method_Name    : gte
	 *  @param first
	 *  @param second
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午6:34:32 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean gte(BigDecimal first, double second){
		return first != null && first.compareTo(BigDecimal.valueOf(second)) >= 0;
	}
	
	/**
	 *  @Description    : first >= second
	 *  @Method_Name    : gte
	 *  @param first
	 *  @param second
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月14日 上午9:49:22 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean gte(double first, BigDecimal second){
		return second != null && BigDecimal.valueOf(first).compareTo(second) >= 0;
	}
	
	
	/**
	 *  @Description    : 判断num是否是100的整数倍
	 *  @Method_Name    : residue
	 *  @param num
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午5:01:35 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean residue(BigDecimal num){
		return num != null && eZero(num.remainder(new BigDecimal(100))) && gtZero(num);
	}
	
	/**
	 *  @Description    : 判断num是否是100的整数倍
	 *  @Method_Name    : residue
	 *  @param num
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午5:05:08 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean residue(int num){
		return (num % 100) == 0 && num > 0;
	}
	
	/**
	 *  @Description    : 判断num是否是100的整数倍
	 *  @Method_Name    : residue
	 *  @param num
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午5:07:13 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean residue(double num){
		return (num % 100) == 0 && num > 0;
	}
	
	/**
	 *  @Description    : 判断num % divisor是否整除,且num > 0
	 *  @Method_Name    : residue
	 *  @param num
	 *  @param divisor
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月13日 下午5:09:00 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean residue(BigDecimal num, BigDecimal divisor){
		return num != null && divisor != null && eZero(num.remainder(divisor)) && gtZero(num);
	}
	
	/**
	 *  @Description    : 判断num % divisor是否整除,且num > 0
	 *  @Method_Name    : residue
	 *  @param num
	 *  @param divisor
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年6月14日 上午9:39:43 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean residue(double num, double divisor){
		return eZero(BigDecimal.valueOf(num).remainder(BigDecimal.valueOf(divisor))) && gtZero(BigDecimal.valueOf(num));
	}
	
	
	public static void main(String[] args) {
		BigDecimal first = new BigDecimal(1);
		BigDecimal second = new BigDecimal(2);
		System.out.println(gtZero(first));
		System.out.println(gtZero(first.multiply(BigDecimal.valueOf(-1))));
		System.out.println(gte(first, second));
		System.out.println("-100 % 20 是否整除" + residue(new BigDecimal("-100")));
		System.out.println("100 % 20 是否整除" + residue(new BigDecimal("100"), new BigDecimal("20")));
		System.out.println(BigDecimal.valueOf(100).negate());
	}
}
