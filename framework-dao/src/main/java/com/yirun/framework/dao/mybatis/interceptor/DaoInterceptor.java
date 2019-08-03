package com.yirun.framework.dao.mybatis.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.yirun.framework.core.annotation.Dao;
import com.yirun.framework.dao.datasource.DynamicDataSource;

@Aspect
@Component
@Order(1)
public class DaoInterceptor {
	 
	@Around(value = "execution(* *com.yirun..dao..impl.*DaoImpl.* (..))", argNames = "pjp")
	public Object doException(ProceedingJoinPoint pjp) {
		Object obj = null;
		try {
			Object target = pjp.getTarget();
			Dao dao = target.getClass().getAnnotation(Dao.class);
			String [] dses = dao.dsKey();
			if (dses != null && dses.length > 0) {
				String curDs = dses[0];
				DynamicDataSource.CONTEXT_HOLDER.set(curDs);
			}
			obj = pjp.proceed();
		} catch (Throwable e) {
		    throw new RuntimeException(e);
		} finally {
			DynamicDataSource.CONTEXT_HOLDER.remove();
		}
		return obj;
	} 
}
