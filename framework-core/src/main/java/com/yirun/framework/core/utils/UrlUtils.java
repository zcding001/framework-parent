package com.yirun.framework.core.utils;

import java.beans.Introspector;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import com.yirun.framework.core.exception.GeneralException;

public abstract class UrlUtils {

	public static String buildRequestPath(HttpServletRequest request) {
		// context info
		String servletPath = request.getServletPath();
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String pathInfo = request.getPathInfo();
		// build
		StringBuilder url = new StringBuilder();
		if (servletPath != null) {
			url.append(servletPath);
			if (pathInfo != null) {
				url.append(pathInfo);
			}
		} else {
			url.append(requestURI.substring(contextPath.length()));
		}

		return url.toString();
	}

	public static String buildUrlFromClass(Class<?> clazz, String[] cutedSuffixes) {
		String pkgName = ClassUtils.getPackageName(clazz);
		String className = Introspector.decapitalize(ClassUtils.getShortName(clazz));
		pkgName = pkgName.replaceFirst("^([a-z]+\\.){3}", "");
		if (pkgName.startsWith(".")) {
			pkgName = pkgName.substring(1);
		}
		String firstPkgName = pkgName.split("\\.")[0];
		if (cutedSuffixes != null) {
			for (String suffix : cutedSuffixes) {
				if (className.endsWith(suffix)) {
					className = className.replaceFirst(suffix + "$", "");
					break;
				}
			}
		}
		String path;
		if (StringUtilsExtend.hasLength(firstPkgName)) {
			path = "/" + firstPkgName + "/" + className;
		} else {
			path = "/" + className;
		}
		return path;
	}

	public static String[] appendUrlSuffixes(String[] urls, String[] suffixes) {
		if (ObjectUtilsExtend.isEmpty(urls) || ObjectUtilsExtend.isEmpty(suffixes)) {
			return urls;
		}
		List<String> finalUrls = new ArrayList<String>();
		for (String perUrl : urls) {
			for (String perSuffix : suffixes) {
				finalUrls.add(perUrl + perSuffix);
			}
		}
		return finalUrls.toArray(new String[finalUrls.size()]);
	}

	public static String appendUrlParameters(String url, Map<String, String> parameters, String... encoding) {
		if (url == null || CollectionUtils.isEmpty(parameters))
			return url;
		StringBuffer urlBuffer = new StringBuffer(url);
		if (url.indexOf("?") < 0) {
			urlBuffer.append("?");
		}
		boolean useEncoding = encoding != null && encoding.length > 0 && StringUtilsExtend.hasLength(encoding[0]);
		try {
			for (String key : parameters.keySet()) {
				if (key == null || parameters.get(key) == null) {
					continue;
				}
				boolean hasOtherParameter = urlBuffer.substring(urlBuffer.indexOf("?") + 1).length() > 0;
				if (hasOtherParameter) {
					urlBuffer.append(hasOtherParameter ? "&" : "");
				}
				urlBuffer.append(key + "=");
				urlBuffer.append(URLEncoder.encode(parameters.get(key), useEncoding ? encoding[0] : "UTF-8"));
			}
		} catch (Exception e) {
			throw new GeneralException("append parameter error", e);
		}
		return urlBuffer.toString();
	}

	public static String getRequestSuffix(HttpServletRequest request) {
		String path = buildRequestPath(request);
		if (StringUtilsExtend.hasLength(path) && path.lastIndexOf(".") > -1) {
			return path.substring(path.lastIndexOf("."));
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("a", "1");
		m.put("b", "1");
		m.put("c", "1");
		System.out.println(appendUrlParameters("http://aa.com/?", m));
	}
}
