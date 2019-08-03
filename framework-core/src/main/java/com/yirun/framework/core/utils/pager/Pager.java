package com.yirun.framework.core.utils.pager;

import org.apache.commons.collections4.ListUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @Description   : 分页信息
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.pager.Pager.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class Pager implements Serializable {

	private static final long serialVersionUID = -1886255970252256895L;

	/**
	 * 当前页号
	 */
	private int currentPage;

	private int startNum;

	private int endNum;

	/**
	 * 每页记录数
	 */
	private int pageSize=10;

	/**
	 * 结果集起始记录数
	 */
	private int startRow;

	/**
	 * 总计页数
	 */
	private int totalPages;

	/**
	 * 总计记录数
	 */
	private int totalRows;

	/**
	 * 无尽模式：如果是无尽模式,不需去查询count，因为客户端是瀑布式的查询
	 * 不需要关心总的记录数是多少，默认是false
	 */
	private boolean infiniteMode=false;
	
	private List<?> data= Collections.EMPTY_LIST;

	public Pager() {
		currentPage = 1;
		startRow = 0;
		totalRows = 0;
		startNum = 0;
		// if (totalRows == 0) {
		// startNum = 0;
		// } else {
		// startNum = 1;
		// }
		endNum = 10;
	}

	/**
	 * 构造函数 通过记录总数和每页记录数计算出相关信息(_totalRows记录总数，onePageSize每页记录数)
	 */
	public Pager(int _totalRows, int onePageSize) {
		setPageSize(onePageSize);
		totalRows = _totalRows;
		//刷新参数
		culParam();
	}




	/**
	 * 构造函数 通过记录总数和每页记录数计算出相关信息
	 */
	public Pager(int _totalRows, int onePageSize, int currentPage) {

		setPageSize(onePageSize);
		totalRows = _totalRows;
		totalPages = totalRows / pageSize;

		int mod = totalRows % pageSize;
		if (mod > 0) {
			totalPages++;
		}
		if (totalPages < currentPage) {
			currentPage = totalPages;
		}
		if (currentPage < 1) {
			currentPage = 1;
		}
		if (totalRows == 0) {
			this.currentPage = 1;
		} else {
			this.currentPage = currentPage;
		}
		startRow = 0;
		if (totalRows == 0) {
			startNum = 0;
		} else {
			startNum = (currentPage - 1) * onePageSize;
		}
		if (totalRows <= pageSize) {
			endNum = totalRows;
		} else {
			if(currentPage == totalPages) {
				endNum = totalRows;
			} else {
				endNum = pageSize * currentPage;
			}
		}
	}

	/**
	 * 第一页
	 */
	public void first() {
		if (totalRows == 0) {
			currentPage = 0;
		} else {
			currentPage = 1;
		}
		startRow = 0;
		if (totalRows == 0) {
			startNum = 0;
		} else {
			startNum = 1;
		}
		if (totalPages <= pageSize) {
			endNum = totalRows;
		} else {
			if(currentPage == totalPages) {
				endNum = totalRows;
			} else {
				endNum = pageSize * currentPage;
			}
		}
	}

	/**
	 * 最后一页
	 */
	public void last() {
		if (totalPages != 0) {
			currentPage = totalPages;
			startRow = (currentPage - 1) * pageSize;
			startNum = startRow + 1;
			endNum = totalRows;
		}
	}

	/**
	 * 下一页
	 */
	public void next() {

		if (currentPage < totalPages && totalPages != 0) {
			currentPage++;
		}
		if (totalPages != 0) {
			startRow = (currentPage - 1) * pageSize;
		}
		startNum = startRow + 1;
		if (currentPage < totalPages && totalPages != 0) {
			endNum = startRow + pageSize;
		} else {
			endNum = totalRows;
		}
	}

	/**
	 * 上一页
	 */
	public void previous() {
		if (currentPage <= 1) {
			first();
		} else {
			currentPage--;
			startRow = (currentPage - 1) * pageSize;
			startNum = startRow + 1;
			if (currentPage < totalPages && totalPages != 0) {
				endNum = startRow + pageSize;
			} else {
				endNum = totalRows;
			}
		}
	}

	/**
	 * 更新页
	 */
	public void refresh(int _currentPage) {
		currentPage = _currentPage;
		if (currentPage > totalPages) {
			last();
		}
		if (currentPage < 0) {
			first();
		}
	}

	/**
	 * 指定页码
	 */
	public void go(int _pageNo) {
		if (_pageNo <= 1) {
			first();
		} else if (_pageNo >= totalPages) {
			last();
			if (totalPages < 1) {
				currentPage = 0;
			}
		} else {
			startRow = (currentPage - 1) * pageSize;
			startNum = startRow + 1;
			endNum = startRow + pageSize;
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getStartRow() {
		return startRow;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
		//重新计算参数
		culParam();
	}

	public int getEndNum() {
		return endNum;
	}

	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}

	public int getStartNum() {
		if (startNum < 0)
			startNum = 0;
		return startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public boolean isInfiniteMode() {
		return infiniteMode;
	}

	public void setInfiniteMode(boolean infiniteMode) {
		this.infiniteMode = infiniteMode;
	}

	/**
	 * 计算相应的参数
	 */
	private void culParam() {
		totalPages = totalRows / pageSize;
		int mod = totalRows % pageSize;
		if (mod > 0) {
			totalPages++;
		}
		if (totalRows == 0||currentPage>totalPages||currentPage<=0) {
			currentPage = 1;
		}
		//计算开始位置
		startRow = (currentPage - 1) * pageSize;
		//重置序号
        startNum=startRow+1;
		if (totalRows <= pageSize) {
			endNum = totalRows;
		} else {
			if (currentPage == totalPages) {
				endNum = totalRows;
			} else {
				endNum = pageSize * currentPage;
			}
		}
	}
}
