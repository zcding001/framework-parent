package com.yirun.framework.core.utils.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.codehaus.jackson.map.ser.std.NullSerializer;

public final class JacksonJsonMapper {

	static volatile ObjectMapper objectMapper = null;

	private JacksonJsonMapper() {
	}

	public static ObjectMapper getInstance() {
		if (objectMapper == null) {
			synchronized (ObjectMapper.class) {
				if (objectMapper == null) {
					StdSerializerProvider ssp = new StdSerializerProvider();
					ssp.setNullValueSerializer(NullSerializer.instance);
					objectMapper = new ObjectMapper(null, ssp, null);
				}
			}
		}
		return objectMapper;
	}
}