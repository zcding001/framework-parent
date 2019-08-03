
package com.yirun.framework.oss.model;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;
/**
 * @Description : OSS的属性加载器
 * @Project : finance
 * @Program Name : com.yirun.framework.oss.model.OSSProLoader
 * @Author : zhongpingtang@hongkun.com.cn
 */
public class OSSProLoader {
	private static final String DEFAULT_STRATEGIES_PATH = "OSSProLoader.properties";
	/**
	 *默认的属性侧率
	 */
	private static final Properties defaultStrategies;

	//在此类加载的时候就把默认的配置加载进来
	static {
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH);
			if (resource==null||!resource.exists()) {
				resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH,OSSBuckets.class);
			}
			defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not load 'OSSProLoader.properties': " + ex.getMessage());
		}
	}



	public OSSProLoader() {
		/*
		Empty
		 */
	}


	/**
	*  @Description    ：获取propertyName对应的key
	*  @Method_Name    ：loadProp
	*  @param propertyName
	*  @return java.lang.String
	*  @Creation Date  ：2018/4/13
	*  @Author         ：zhongpingtang@hongkun.com.cn
	*/
	private  String loadProp(String propertyName){
		return defaultStrategies.getProperty(propertyName, null);
	}

	/**
	*  @Description    ：初始化oss参数
	*  @Method_Name    ：initOSSConfig
	*  @param ossConfig
	*  @return com.yirun.framework.oss.model.OssConfig
	*  @Creation Date  ：2018/4/13
	*  @Author         ：zhongpingtang@hongkun.com.cn
	*/
	public  OssConfig initOSSConfig(OssConfig ossConfig) {
		ossConfig.setOSSEndpoint(loadProp("OSSEndpoint"));
		ossConfig.setAccessId(loadProp("accessId"));
		ossConfig.setAccessKey(loadProp("accessKey"));
		return ossConfig;
	}


}
