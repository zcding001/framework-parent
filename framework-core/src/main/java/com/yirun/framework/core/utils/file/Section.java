package com.yirun.framework.core.utils.file;

import java.io.File;

/**
 * @Description   : start <= x <= end
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.file.Section.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class Section {
	
	/**
	 * 文件开始值
	 */
	private long start;
	
	/**
	 * 文件结束位置
	 */
	private long end;
	
	/**
	 * 段名称
	 */
	private String sectionName;
	
	/**
	 * 待拆分的源文件路径
	 */
	private File sourceFile;
	
	/**
	 * 分割后的文件夹路径
	 */
	private File toDir;

	/**
	 * 每个段的大小
	 */
	private long sectionSize; 
	
	public Section() {
	}

	public Section(File sourceFile,File toDir,long start, long end, String sectionName) {
		super();
		this.start = start;
		this.end = end;
		this.sectionName = sectionName;
		this.sourceFile = sourceFile;
		this.toDir = toDir;
		this.getSectionSize();
	}


	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public long getSectionSize() {
		sectionSize = this.getEnd() - this.getStart();
		if(this.getStart() == 0) {
			return sectionSize;
		} else {
			return sectionSize + 1;
		}
	}

	public void setSectionSize(long sectionSize) {
		this.sectionSize = sectionSize;
	}

	public File getToDir() {
		return toDir;
	}

	public void setToDir(File toDir) {
		this.toDir = toDir;
	}
	
	/**
	 * @description  取得目的段文件
	 * @return
	 */
	public File getDestSectionFile() {
		return new File(getToDir(), getSectionName());
	}
    

}
