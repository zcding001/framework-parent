package com.yirun.framework.dao.mybatis.resultHandler;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

@SuppressWarnings("rawtypes")
public class CountResultHandler implements ResultHandler {
	private int count;

	public void handleResult(ResultContext context) {
		count = Integer.parseInt("" + context.getResultObject());
	}

	public int getCount() {
		return count;
	}
}