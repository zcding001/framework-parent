package com.yirun.framework.dao.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @Description   : 动态数据源
 * @Project       : framework-dao
 * @Program Name  : com.yirun.framework.dao.datasource.DynamicDataSource.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	public static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

	@Override
	protected Object determineCurrentLookupKey() {
		return (String) CONTEXT_HOLDER.get();
	}
}
