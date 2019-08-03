package com.yirun.framework.core.utils.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * @Description   : 文件分割接口
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.file.FileSplit.java
 * @Author        : imzhousong@gmail.com 周松
 */
public interface FileSplit {

	/**
	 * 段的分割符号
	 */
	public static String SECTION_SPLIT_STUFF = "_";

	/**
	 *  缓存区setter/getter，适当的设计该值 bufferSize 有助于提高性能
	 */
	public void setBufferSize(int bufferSize);

	public int getBufferSize();

	/**
	 *  @Description    : 将文件分割位为指定份数，返回每份文件大小
	 *  @Method_Name    : splitFileToSection
	 *  @param file			源文件
	 *  @param toDir		分段后的文件夹路径
	 *  @param sectionNum	份数
	 *  @return         : Vector<Section>
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public Vector<Section> splitFileToSection(File file, File toDir, int sectionNum);
	
	/**
	 *  @Description    : 将文件按段数分割
	 *  @Method_Name    : splitFileBySNum
	 *  @param file				源文件
	 *  @param toDir			产生的段到指定的文件夹下
	 *  @param sectionNum		拆分成的段数
	 *  @param isMarkSection	是否生成头标识(将不能文件名区分文件的顺序)
	 *  @param threadNum		同时拆分的线程数(同一个磁盘单线程效率比多线程效率高很多)
	 *  @throws IOException
	 *  @return         : List<File>
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public List<File> splitFileBySNum(File file, File toDir,int sectionNum, boolean isMarkSection,Integer threadNum) throws IOException;
	
	/**
	 *  @Description    : 将文件按段的大小分割
	 *  @Method_Name    : splitFileBySSize
	 *  @param file			源文件
	 *  @param toDir		产生的段到指定的文件夹下
	 *  @param sectionSize	按大小拆分文件
	 *  @param isMarkSection是否生成头标识(将不能文件名区分文件的顺序)
	 *  @param threadNum	同时拆分的线程数(同一个磁盘单线程效率比多线程效率高很多)
	 *  @throws IOException
	 *  @return         : List<File>
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public List<File> splitFileBySSize(File file, File toDir,long sectionSize, boolean isMarkSection,Integer threadNum) throws IOException;
	
 	
	/**
	 *  @Description    : 文件合并
	 *  @Method_Name    : fileCombine
	 *  @param listFiles	待合并的文件
	 *  @param isMarkSection是否使用头标识区别文件
	 *  @param fileDest		合并后的文件
	 *  @return
	 *  @return         : boolean
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public  boolean fileCombine(List<File> listFiles,boolean isMarkSection,File fileDest);
	
	/**
	 *  @Description    : 文件合并
	 *  @Method_Name    : fileCombile
	 *  @param sourceDir		待合并的文件夹路径
	 *  @param isMarkSection	是否使用头标识区别文件
	 *  @param fileDest			合并后的文件
	 *  @return         : boolean
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public boolean fileCombile(File sourceDir,boolean isMarkSection,File fileDest);

}
