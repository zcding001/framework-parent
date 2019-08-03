package com.yirun.framework.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @Description : Spring工具类StringUtils扩展
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.utils.StringUtilsExtend.java
 * @Author : imzhousong@gmail.com 周松
 */
public abstract class StringUtilsExtend extends StringUtils {

	/**
	 * @Description:替换第几个字符串
	 */
	public static String replace(String source, String target, String replacement, int sequence) {
		int tempSequence = 0;
		int i = 0;
		int iterateIndex = -1;
		StringBuffer replacedString = new StringBuffer();
		while ((iterateIndex = source.indexOf(target, i)) > 0) {
			tempSequence++;
			if (sequence == tempSequence) {
				replacedString.append(source.substring(0, iterateIndex));
				replacedString.append(replacement);
				replacedString.append(source.substring(iterateIndex + target.length()));
				break;
			}
			i = iterateIndex + target.length();
		}
		return replacedString.toString();
	}

	/**
	 * @Description:str是否存在strChar字符
	 */
	public static int safeIndexOf(String str, String strChar) {
		if (!hasLength(str)) {
			return -1;
		}
		return str.indexOf(strChar);
	}

	/**
	 * @Description:去除字符串空格
	 */
	public static String safeTrim(String str) {
		if (!hasLength(str)) {
			return str;
		}
		return str.trim();
	}

	/**
	 * @Description:字符串的字节数组长度
	 */
	public static int getByteSize(String str) {
		if (str != null) {
			return str.getBytes().length;
		}
		return 0;
	}

	/**
	 * @Description:判断是否为0-9的数字字符串, null和""都为false
	 */
	public static boolean isNumeric(String str) {
		if (!hasLength(str)) {
			return false;
		}
		return Pattern.matches("[0-9]+", str);
	}

	/**
	 * @Description:格式化HTTP站点地址字符串，去掉http://
	 */
	public static String websitFormat(String websit) {

		if (!hasLength(websit)) {
			return null;
		}

		return websit.replaceAll("http://", "").trim().replaceAll("https://", "").trim();
	}

	/**
	 * @Description:半角转全角
	 */
	public static String toSBC(String input) {
		if (!hasLength(input)) {
			return null;
		}
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	/**
	 * @Description:全角转半角
	 */
	public static String toDBC(String input) {
		if (!hasLength(input)) {
			return null;
		}
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		String returnString = new String(c);
		return returnString;
	}

	public static String fill(String str, int width, String... placeholder) {
		StringBuffer filled = new StringBuffer();
		int zeroCount = (str == null ? width : width - str.length());
		String finalPlaceholder = "0";
		if (!ObjectUtils.isEmpty(placeholder)) {
			finalPlaceholder = placeholder[0];
		}
		for (int i = 0; i < zeroCount; i++) {
			filled.append(finalPlaceholder);
		}
		filled.append(str);
		return filled.toString();
	}

	/**
	 * 将str将多个分隔符进行切分，
	 * 
	 * 示例：StringTokenizerUtils.split("1,2;3 4"," ,;"); 返回: ["1","2","3","4"]
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static String[] split(String str, String seperators) {
		StringTokenizer tokenlizer = new StringTokenizer(str, seperators);
		List result = new ArrayList();

		while (tokenlizer.hasMoreElements()) {
			Object s = tokenlizer.nextElement();
			result.add(s);
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	/**
	 * 
	 * @Description : caoxb 方法描述：根据长度加密
	 * @Method_Name : getEllipsis
	 * @param str
	 * @param preLength
	 * @param aftLength
	 * @return
	 * @return : String
	 * @Creation Date : 2017年6月12日 下午4:37:54
	 * @Author : caoxinbang@hongkun.com.cn 曹新帮
	 */
	public static String getEllipsis(String str, int preLength, int aftLength) {
		StringBuffer newStr = new StringBuffer();
		if (isEmpty(str)) {
			return "";
		}
		if (str.length() < preLength || str.length() < aftLength) {
			for (int i = 0; i < str.length(); i++) {
				newStr.append("*");
			}
			return newStr.toString();
		}
		for (int i = 0; i < preLength; i++) {
			newStr.append("*");
		}
		newStr.append(str.substring(preLength, str.length() - aftLength));
		for (int i = 0; i < aftLength; i++) {
			newStr.append("*");
		}
		return newStr.toString();
	}

	/**
	 * 
	 * @Description : caoxb 方法描述:首字母大写,后面字母小写
	 * @Method_Name : captureName
	 * @param name
	 * @return
	 * @return : String
	 * @Creation Date : 2017年6月12日 下午4:37:15
	 * @Author : caoxinbang@hongkun.com.cn 曹新帮
	 */
	public static String captureName(String str) {
		if (isEmpty(str)) {
			return "";
		}
		str = str.substring(0, 1).toUpperCase() + str.substring(1, str.length()).toLowerCase();
		return str;
	}

	/**
	 *  @Description    : 校验codes不是含有非法的UUID集合
	 *  @Method_Name    : isUUID
	 *  @param codes
	 *  @return         : boolean
	 *  @Creation Date  : 2017年11月22日 下午3:12:53 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean isUUID(String codes) {
		String regex = "^[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}$";
		String[] arr = codes.split(",");
		for (String code : arr) {
			if (!code.matches(regex)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	 *  @Description    : 过滤富文本编辑器html标签
	 *  @Method_Name    : delHTMLTag;
	 *  @param htmlStr
	 *  @return
	 *  @return         : String;
	 *  @Creation Date  : 2018年5月21日 下午8:03:20;
	 *  @Author         : xinbangcao@hongkun.com.cn 曹新帮;
	 */
	public static String delHTMLTag( String htmlStr ){
		if(StringUtils.isEmpty(htmlStr)) {
			return htmlStr;
		}
		/* 去掉style标签 */
		Pattern p_style = Pattern
				  .compile( "<style[^>]*?>[\\\\s\\\\S]*?<\\\\/style>", Pattern.CASE_INSENSITIVE );
		Matcher m_style = p_style.matcher( htmlStr );
		htmlStr = m_style.replaceAll( "" ); 
		/* 去掉html标签 */
		Pattern p_html	= Pattern.compile( "<[^>]+>", Pattern.CASE_INSENSITIVE );
		Matcher m_html	= p_html.matcher( htmlStr );
		htmlStr = m_html.replaceAll( "" );
		/* 去掉空格 */
		Pattern p_space = Pattern
				  .compile( "<a>\\\\s*|\\t|\\r|\\n</a>", Pattern.CASE_INSENSITIVE );
		Matcher m_space = p_space.matcher( htmlStr );
		htmlStr = m_space.replaceAll( "" );
		/* 去掉<p>标签<br></br>标签和<>之间内容 */
		htmlStr.replaceAll( "<p .*?>", "\r\n" );
		htmlStr.replaceAll( "<br\\s*/?>", "\r\n" );
		htmlStr.replaceAll( "\\<.*?>", "" );
		return(htmlStr.trim());
	}
}
