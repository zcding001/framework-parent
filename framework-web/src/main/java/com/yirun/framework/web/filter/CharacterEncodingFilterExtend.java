package com.yirun.framework.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * @Description   : 编码过滤，后期可扩展
 * @Project       : framework-web
 * @Program Name  : com.yirun.framework.web.ExtendCharacterEncodingFilter.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class CharacterEncodingFilterExtend extends CharacterEncodingFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		super.doFilterInternal(request, response, filterChain);
	}
}
