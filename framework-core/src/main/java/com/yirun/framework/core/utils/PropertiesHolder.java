package com.yirun.framework.core.utils;

import com.yirun.framework.core.commons.Constants;
import com.yirun.framework.core.exception.GeneralException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 用于持有Properties,将Propereis变成静态方法使用. PropertiesHolder只能持有一个properties
 * 
 * <pre>
 * PropertiesHolder初始化
 * 
 * <b>spring初始化:</b>
 * &lt;bean class="cn.org.rapid_framework.util.holder.PropertiesHolder">
 * 		&lt;property name="properties" ref="applicationProperties"/>
 * &lt;/bean>
 * 
 * <b>java API初始化:</b>
 * new PropertiesHolder().setProperties(cache)
 * 
 * <b>如果需要重新初始化Properties,需要</b>
 * PropertiesHolder.clearHolder();
 * new PropertiesHolder().setProperties(cache)
 * </pre>
 */
@Component("propertiesHolder")
public class PropertiesHolder implements ApplicationContextAware, ServletContextAware, InitializingBean {

//	private static Log log = LogFactory.getLog("PropertiesHolderOld");
	private static final Logger logger = Logger.getLogger(PropertiesHolder.class);

	/**
	 * 缓存properties
	 */
	private static Properties properties = new Properties();

	private ApplicationContext applicationContext = null;

	private ServletContext servletContext = null;

	private static boolean isLogProperty = false;

	public static void setProperties(Properties properies) {
		clearHolder();
		PropertiesHolder.properties = properies;
	}

	public static void addProperties(Properties properies) {
		properties.putAll(properies);
	}

	public static Properties getProperties() {
		return properties;
	}

	/**
	 * 清空 holder,只有清空Holder才可以重新设置 Properties
	 */
	public static void clearHolder() {
		properties.clear();
	}

	public static void setProperty(String key, String value) {
		properties.put(key, value);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initApplicationProperties(applicationContext);

		/**
		 * 设置系统常用资源信息
		 */
		if (servletContext != null) {
			servletContext.setAttribute(Constants.STATIC_HOST, getProperty("static.host"));
		}

		/**
		 * 根据需求，加载以context_开始的properties key=value到servletContext中
		 */
		Properties p = getProperties();
		if (p != null) {
			Enumeration<Object> e = p.keys();
			while (e.hasMoreElements()) {
				String key = String.valueOf(e.nextElement());
				if (key.startsWith("context_")) {
					if (servletContext != null) {
						servletContext.setAttribute(key, getProperty(key));
					}
				}
				if (isLogProperty) {
					logger.error(key + "---" + getProperty(key));
				}
			}
		}
		logger.info("PropertiesHolder holded properties:" + getProperties());
	}

	/**
	 * 加载系统必须的properties
	 * 
	 * @throws
	 */
	@SuppressWarnings({ "unchecked" })
	private void initApplicationProperties(ApplicationContext context) {

		// 获取服务器运行时的根目录
		String serverBaseDir = System.getProperty(Constants.SERVER_TOMCAT_BASE);
		// 开始监控
		long start = System.currentTimeMillis();
		try {
			// 获取System Properties
			Properties systemProperties = new Properties();
			systemProperties.put(Constants.SERVER_BASE, serverBaseDir != null ? serverBaseDir : "");
			systemProperties.putAll(System.getProperties());
			List<Properties> listJarProp = new ArrayList<Properties>();
			String path = PropertiesHolder.class.getResource("/").getFile();
			File jarPath = null;
			if (servletContext != null) {
				jarPath = new File(servletContext.getRealPath("/"));
			} else {
				jarPath = new File(path);
			}

			/**
			 * 加载jar 包中的所有 properties
			 */
			List<String> listFileNames = getAllJarProperties(jarPath);
			for (String string : listFileNames) {
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(string);
				if (in != null) {
					logger.info("jar-properties====" + string);
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
					Properties p = new Properties();
//					p.load(in);
					p.load(bufferedReader);
					listJarProp.add(p);
				}
			}

			// 获取非加密的Properties
			Resource[] classpathResources = context.getResources("classpath*:application*.properties");
			classpathResources=
					(Resource[]) ArrayUtils.addAll(classpathResources, context.getResources("classpath*:env*.properties"));
			if (logger.isInfoEnabled()) {
				StringBuffer propertiesInfo = new StringBuffer();
				propertiesInfo.append("\n==============loaded properties files=================\n");
//				add by zc.ding start on 2017.3.27
				for (Resource r : classpathResources) {
					propertiesInfo.append(r.toString() + "\n");
					InputStream in = r.getInputStream();
					if (in != null) {
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
						Properties p = new Properties();
						p.load(bufferedReader);
//						p.load(in);
						listJarProp.add(p);
					}
				}
//				add by zc.ding end on 2017.3.27
				logger.info(propertiesInfo.toString());
			}

			// 将已经加载的Properties合并
			List<Properties> loadedProperties = new ArrayList<Properties>();
			loadedProperties.add(systemProperties);

			// 合并Properties
			Properties result = new Properties();
			for (Properties pJar : listJarProp) {
				Set<Object> setKeys = pJar.keySet();
				for (Iterator<Object> it = setKeys.iterator(); it.hasNext();) {
					Object obj = it.next();
					if (result.get(obj) == null) {
						result.put(obj, pJar.get(obj));
					}
				}
			}

			// 初始化到工具类
			addProperties(result);
		} catch (Exception e) {
			throw new GeneralException("load application properties error", e);
		} finally {
			// 监控介绍计算时间
			long end = System.currentTimeMillis();
			if (logger.isInfoEnabled()) {
				logger.info(" scan application properties consume time(millisecond) : " + (end - start));
			}
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * String regex = "application.+\\.properties";
	 * 
	 * @param PropertiesHolder设定文件
	 * @return List<Properties> DOM对象
	 * @throws
	 */
	public static List<String> getSpecifiedFileFromJar(File file, String regex) {
		List<String> lists = new ArrayList<String>();
		try {
			if (StringUtils.isEmpty(regex)) {
				return lists;
			}
			Pattern p = Pattern.compile(regex);
			JarFile jarFile = new JarFile(file.getAbsolutePath());
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				Matcher m = p.matcher(name);
				if (m.matches()) {
					lists.add(name);
				}
			}
			
			jarFile.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		return lists;
	}

	/**
	 * 
	 * @description 取得所有 jar包中的properties
	 * @param PropertiesHolder设定文件
	 * @return List<String> DOM对象
	 * @throws
	 */
	public static List<String> getAllJarProperties(File parentFile) {
		List<String> lists = new ArrayList<String>();
		List<File> listJars = (List<File>) FileUtils.listFiles(parentFile, new String[] { "jar" }, true);
		for (File file : listJars) {
			lists.addAll(getSpecifiedFileFromJar(file, "application.+\\.properties"));
		}
		return lists;
	}

	/**
	 *  获取布尔值
	 */
	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = getProperty(key);
		if (StringUtils.isBlank(value)) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	public static Boolean getBoolean(String key) {
		String value = getProperty(key);
		if (StringUtils.isBlank(value)) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	/**
	 *  获取Double值
	 */
	public static double getDouble(String key, double defaultValue) {
		return getDouble(key) == null ? defaultValue : getDouble(key);
	}

	public static Double getDouble(String key) {
		String value = getProperty(key);
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return Double.parseDouble(value);
	}

	/**
	 *  获取Float值
	 */
	public static float getFloat(String key, float defaultValue) {
		return getFloat(key) == null ? defaultValue : getFloat(key);
	}

	public static Float getFloat(String key) {
		String value = getProperty(key);
		if(StringUtils.isEmpty(value)) {
			return null;
		}
		return Float.parseFloat(value);
	}

	/**
	 *  获取int值
	 */
	public static int getInt(String key, int defaultValue) {
		String value = getProperty(key);
		if(StringUtils.isEmpty(value)) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}
	
	public static Integer getInteger(String key) {
		String value = getProperty(key);
		if(StringUtils.isEmpty(value)) {
			return null;
		}
		return Integer.parseInt(value);
	}

	public static int[] getIntArray(String key) {
		return toIntArray(getStringArray(key));
	}
	private static int[] toIntArray(String[] array) {
		int[] result = new int[array.length];
		for(int i = 0; i < array.length; i++) {
			result[i] = Integer.parseInt(array[i]);
		}
		return result;
	}
	
	public static long getLong(String key, long defaultValue) {
		String value = getProperty(key);
		if(StringUtils.isEmpty(value)) {
			return defaultValue;
		}
		return Long.parseLong(value);
	}

	public static Long getLong(String key) {
		String value = getProperty(key);
		if(StringUtils.isEmpty(value)) {
			return null;
		}
		return Long.parseLong(value);
	}

	public static String getNullIfBlank(String key) {
 		String value = getProperty(key);
		if(StringUtils.isEmpty(value) || StringUtils.isEmpty(value.trim())) {
			return null;
		}
		return value;
	}

	public static String getNullIfEmpty(String key) {
		String value = getProperty(key);
		if(StringUtils.isEmpty(value)) {
			return null;
		}
		return value;
	}

	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static boolean getRequiredBoolean(String key) throws IllegalStateException {
		return Boolean.parseBoolean(getRequiredProperty(key));
	}

	public static double getRequiredDouble(String key) throws IllegalStateException {
		return Double.parseDouble(getRequiredProperty(key));
	}

	public static float getRequiredFloat(String key) throws IllegalStateException {
		return Float.parseFloat(getRequiredProperty(key));
	}

	public static int getRequiredInt(String key) throws IllegalStateException {
		return Integer.parseInt(getRequiredProperty(key));
	}

	public static long getRequiredLong(String key) throws IllegalStateException {
		return Long.parseLong(getRequiredProperty(key));
	}

	public static String getRequiredProperty(String key) throws IllegalStateException {
		String value = getProperty(key);
		if(StringUtils.isBlank(value)) {
			throw new IllegalStateException("required property is blank by key="+key);
		}
		return value;
	}

	public static Properties getStartsWithProperties(String prefix) {
		if (prefix == null){
			throw new IllegalArgumentException("'prefix' must be not null");
		}
		Properties props = getProperties();
		Properties result = new Properties();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			String key = (String) entry.getKey();
			if (key != null && key.startsWith(prefix)) {
				result.put(key.substring(prefix.length()), entry.getValue());
			}
		}
		return result;
	}

	public static String[] getStringArray(String key) {
		String v = getProperty(key);
		if (v == null) {
			return new String[0];
		} else {
			return org.springframework.util.StringUtils.tokenizeToStringArray(v, ", \t\n\r\f");
		}
	}

	public static URL getURL(String key) throws IllegalArgumentException {
		try {
			return new URL(getProperty(key));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Property " + key + " must be a valid URL (" + getProperty(key) + ")");
		}
	}
}
