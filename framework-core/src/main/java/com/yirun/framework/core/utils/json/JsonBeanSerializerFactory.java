package com.yirun.framework.core.utils.json;

import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.ser.BeanSerializerFactory;

/**
 * @Description   : 重写jackson 工厂类，增加过滤与仅包含
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.json.JsonBeanSerializerFactory.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class JsonBeanSerializerFactory extends BeanSerializerFactory {

	public final static JsonBeanSerializerFactory instance = new JsonBeanSerializerFactory(null);

	private Object filterId;

	protected JsonBeanSerializerFactory(Config config) {
		super(config);
	}

	@Override
	protected synchronized Object findFilterId(SerializationConfig config, BasicBeanDescription beanDesc) {
		return getFilterId();
	}

	public Object getFilterId() {
		return filterId;
	}

	public void setFilterId(Object filterId) {
		this.filterId = filterId;
	}
}
