package com.yirun.framework.core.utils.pager;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;

import com.yirun.framework.core.utils.CommonUtils;

/**
 * @Description   : 分页信息读取工具
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.pager.PageHelper.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class PageHelper {

	/**
	 * 默认每页记录数
	 */
	private static int DEFAULT_PAGESIZE = 10;

	/**
	 * 操作
	 */
	public static String FIRST_METHOD = "first";

	public static String PREVIOUS_METHOD = "previous";

	public static String NEXT_METHOD = "next";

	public static String LAST_METHOD = "last";

	public static String GO_METHOD = "go";

	/**
	 *  @Description    : 得到Pager
	 *  @Method_Name    : getPager
	 *  @param param
	 *  @param totalRows
	 *  @param onePageSize
	 *  @return         : Pager
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public static Pager getPager(PagerParam param, int totalRows, int onePageSize) {
		Pager pager = new Pager(totalRows, onePageSize);
		String currentPage = param.getCurrentPage();
		if (currentPage != null && !currentPage.equals("")) {
			pager.refresh(Integer.parseInt(currentPage));
		}
		String pagerMethod = param.getPagerMethod();
		if (pagerMethod != null && !"".equals(currentPage)) {
			if (pagerMethod.equals(FIRST_METHOD)) {
				pager.first();
			} else if (pagerMethod.equals(PREVIOUS_METHOD)) {
				pager.previous();
			} else if (pagerMethod.equals(NEXT_METHOD)) {
				pager.next();
			} else if (pagerMethod.equals(LAST_METHOD)) {
				pager.last();
			} else if (pagerMethod.equals(GO_METHOD)) {
				pager.go(Integer.parseInt(currentPage));
			}
		}
		return pager;
	}

	/**
	 *  @Description    : 得到Pager(默认的PageSize)
	 *  @Method_Name    : getPager
	 *  @param param
	 *  @param totalRows
	 *  @return         : Pager
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public static Pager getPager(PagerParam param, int totalRows) {
		return getPager(param, totalRows, DEFAULT_PAGESIZE);
	}
	/**
	 * 
	 *  @Description    : 内存分页
	 *  @Method_Name    : getCurrentPager
	 *  @param list
	 *  @param pager
	 * @return 
	 *  @return
	 *  @return         : Pager
	 *  @Creation Date  : 2018年1月11日 上午9:48:49 
	 *  @Author         : xuhuiliu@honghun.com.cn 刘旭辉
	 */
	public static <T> Pager getCurrentPager(List<T> list, Pager pager){
		if(CommonUtils.isNotEmpty(list)){
			pager = pager==null?new Pager(list.size(),10):pager;
			List<T> result = new ArrayList<T>();
			int startNum =pager.getStartRow();
			int endNum = pager.getPageSize() + pager.getStartRow()-1;
			int maxNum = list.size()-1;
			for(int i=startNum;i<=endNum;i++){
				if(i<=maxNum){
					T obj =  (T) list.get(i);
					if(obj!=null){
						result.add(obj);
					}
				}
			}
			pager.setData(result);
			pager.setTotalRows(list.size());
		}
		return pager;
	}
}
