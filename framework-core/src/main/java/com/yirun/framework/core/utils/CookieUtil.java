package com.yirun.framework.core.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Described	: cookie工具类
 * @author 		: zc.ding
 */
public class CookieUtil {

	/**
	 * 路径
	 */
	private static final String DEFAULT_PATH = "/";
	/**
	 * 有效期"会话"
	 */
	private static final int DEFAULT_MAX_AGE = -1;
	
	/**
	 * @Described			: 创建cookie
	 * @author				: zc.ding
	 * @date 				: 2016年12月29日
	 * @param name cookie名称
	 * @param value cookie值
	 * @return
	 */
	public static Cookie createCookie(String name, String value){
		return createCookie(name, value, null, DEFAULT_MAX_AGE, DEFAULT_PATH);
	}
	
	/**
	 * 创建指定有效期的cookie对象
	 * @author	 zc.ding
	 * @since 	 2017年5月19日
	 * @param name cookie名称
	 * @param value cookie值
	 * @param maxAge cookie有效期
	 * @return
	 */
	public static Cookie createCookie(String name, String value, int maxAge){
		return createCookie(name, value, null, maxAge, DEFAULT_PATH);
	}
	
	/**
	 * @Described			: 创建cookie
	 * @author				: zc.ding
	 * @date 				: 2016年12月29日
	 * @param name cookie名称
	 * @param value cookie值
	 * @param domain 域
	 * @param maxAge 有效期 
	 * @param path 路径
	 * @return
	 */
	public static Cookie createCookie(String name, String value, String domain, int maxAge, String path){
		Cookie cookie = new Cookie(name, value);
		cookie.setDomain(HttpSessionUtil.getRequest().getServerName());
		cookie.setPath(DEFAULT_PATH);
		if(domain != null && domain.length() > 0){
			cookie.setDomain(domain);
		}
		if(path != null && path.length() > 0){
			cookie.setPath(path);
		}
		if(maxAge > 0){
			cookie.setMaxAge(maxAge);
		}
		return cookie;
	}
	
	/**
	 * @Described			: 获得指定name的cookie
	 * @author				: zc.ding
	 * @date 				: 2016年12月29日
	 * @param request
	 * @param name cookie的名称
	 * @return
	 */
	public static Cookie getCookie(HttpServletRequest request, String name){
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for(Cookie cookie : cookies){
				if(name != null && name.equals(cookie.getName())){
					return cookie;
				}
			}
		}
		return null;
	}
	
	/**
	 * @Described			: 获得指定name的cookie
	 * @author				: zc.ding
	 * @date 				: 2016年12月29日
	 * @param name cookie的名称
	 * @return
	 */
	public static Cookie getCookie(String name){
		Cookie[] cookies = HttpSessionUtil.getRequest().getCookies();
		if(cookies != null){
			for(Cookie cookie : cookies){
				if(name != null && name.equals(cookie.getName())){
					return cookie;
				}
			}
		}
		return null;
	}
	
	/**
	 * 加载指定cookie的Value值
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @param request
	 * @param name cookie名称
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String name){
		Cookie cookie = getCookie(request, name);
		if(cookie != null){
			return cookie.getValue();
		}
		return null;
	}
	
	/**
	 * 加载指定cookie的Value值
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @param name cookie名称
	 * @return
	 */
	public static String getCookieValue(String name){
		Cookie cookie = getCookie(name);
		if(cookie != null){
			return cookie.getValue();
		}
		return null;
	}
	
}
