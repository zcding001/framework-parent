package com.yirun.framework.dao.mybatis;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.Resource;

public class MybatisConfig extends SqlSessionFactoryBean {
	//加载所有module下的vo类为默认别名类
	Resource[]  aliasModuleClass ;
	
	public void setAliasModuleClass(Resource[] aliasModuleClass) {
		this.aliasModuleClass = aliasModuleClass;
	}

	/**
	 * 加载所有module下的vo类为默认别名类
	 */
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
    }
}




























