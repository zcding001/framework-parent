package com.yirun.framework.core.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @Description   : 文件分割实现类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.file.FileSplitImpl.java
 * @Author        : imzhousong@gmail.com 周松
 * <p>
 *  磁盘:希捷 ST500DM002-1BD142 ( 500 GB / 7200 转/分 )
 *  文件大小：5.42 GB (5,826,021,376 字节)
 * 【1】 直接用ctrl+c.ctrl+v 耗时:150 秒 (写在同一个磁盘G 下面)  平均 37M/秒
 * 【2】 同时10个线程，将文件切分为10份 耗时: 200秒 (写在同一个磁盘G 下面)  平均 27M/秒
 * 【3】 同时10个线程，将文件切分为10份 耗时: 240 秒 (写在磁盘G,E两个磁盘下，每个磁盘各5个段)  平均 23M/秒
 * 【4】单线程 将文件切分为10份  耗时:133秒 (写在同一个磁盘G 下面)  平均 41M/秒
 * 测试结果：单线程操作IO 比多线程操作效率更高.
 * </p>
 */
public class FileSplitImpl implements FileSplit {

	private static Logger logger = Logger.getLogger(FileSplitImpl.class);

	/**
	 * 该值的大小 设置根据文件大小而定，适当的设置该值可以提高性能 (byte)
	 */
	public int bufferSize = 1024 * 1024 * 10; // 默认10M

	private static ExecutorService executorService = Executors.newFixedThreadPool(10);

	@Override
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	public int getBufferSize() {
		return this.bufferSize;
	}

	/**
	 *  @Description    : 分割文件为多少份(取最少的份数)
	 *  @Method_Name    : splitFileLess
	 *  @param file			被分割的文件
	 *  @param sectionSize	段大小(字节) byte
	 *  @return         : long
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	private long splitFileLess(File file, Long sectionSize) {
		if (file == null || !file.isFile() || !file.exists()) {
			throw new RuntimeException("file does't not exist!");
		}
		long fileSize = file.length();
		if (sectionSize == null || sectionSize <= 0) {
			sectionSize = fileSize;
		}
		if (fileSize <= sectionSize) {
			return 1;
		}
		long section = fileSize / sectionSize;
		return section;
	}

	@Override
	public Vector<Section> splitFileToSection(File file, File toDir, int sectionNum) {
		Vector<Section> lists = new Vector<Section>();
		if (file == null || !file.isFile() || !file.exists()) {
			throw new RuntimeException("file does't exist!");
		}

		long fileSize = file.length();
		if (sectionNum <= 1) {
			lists.add(new Section(file, toDir, 0, file.length(), file.getName() + SECTION_SPLIT_STUFF + "0"));
			return lists;
		}

		long everySectionSize = fileSize / sectionNum;
		long surplusFileSize = fileSize - everySectionSize * sectionNum;
		// 最后一个段的大小
		long lastSectionSize = everySectionSize;
		if (surplusFileSize > 0) {
			lastSectionSize = everySectionSize + surplusFileSize;
		}

		long start = 0;
		long end = 0;
		for (int i = 0; i < sectionNum; i++) {
			start = i * everySectionSize;
			if (i == sectionNum - 1) { // 最后一个段
				end = start + lastSectionSize;
			} else {
				end = start + everySectionSize;
			}
			long startTrue = 0;
			if (i == 0) {
				startTrue = start;
			} else {
				startTrue = start + 1;
			}

			String fileName = file.getName() + SECTION_SPLIT_STUFF + startTrue;
			Section ss = new Section(file, toDir, startTrue, end, fileName);
			ss.setSourceFile(file);
			lists.add(ss);
		}
		return lists;
	}

	@Override
	public List<File> splitFileBySNum(File file, File toDir, int sectionNum, boolean isMarkSection, Integer threadNum)
			throws IOException {
		if (file == null || !file.exists() || !file.isFile()) {
			throw new RuntimeException(" file does't exist!");
		}
		if (toDir == null) {
			throw new RuntimeException("toDir can't null!");
		}

		// 单线程
		if (threadNum == null || threadNum.intValue() <= 1) {
			return splitFileBySNum(file, toDir, sectionNum, isMarkSection);
		} else {
			List<File> destFile = new ArrayList<File>();
			// 多线程
			Vector<Section> lists = splitFileToSection(file, toDir, sectionNum);
			final Semaphore semp = new Semaphore(threadNum);
			for (Section section : lists) {
				destFile.add(section.getDestSectionFile());
				executorService.execute(new FileSectionHandler(bufferSize, section, isMarkSection, semp));
			}
			return destFile;
		}
	}

	@Override
	public List<File> splitFileBySSize(File file, File toDir, long sectionSize, boolean isMarkSection, Integer threadNum)
			throws IOException {
		if (file == null || !file.exists()) {
			throw new RuntimeException(" file does't exist!");
		}
		// 取得被分得的份数
		long fileCount = splitFileLess(file, sectionSize);

		if (fileCount > Integer.MAX_VALUE) {
			throw new RuntimeException(" file section num no more than:" + Integer.MAX_VALUE
					+ "  please reset sectionSize!");
		}
		// 单线程
		if (threadNum == null || threadNum.intValue() <= 1) {
			return splitFileBySNum(file, toDir, Integer.parseInt(String.valueOf(fileCount)), isMarkSection);
		} else {
			// 多线程
			return splitFileBySNum(file, toDir, Integer.parseInt(String.valueOf(fileCount)), isMarkSection, threadNum);
		}

	}

	/**
	 *  @Description    : 根据段数来拆分文件
	 *  @Method_Name    : splitFileBySNum
	 *  @param file			源文件
	 *  @param toDir		生成文件路径文件夹
	 *  @param sectionNum	产生的段数
	 *  @param isMarkSection是否在头信息中加段大小标识
	 *  @throws IOException
	 *  @return         : List<File>生成的段文件
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	@SuppressWarnings("resource")
	private List<File> splitFileBySNum(File file, File toDir, int sectionNum, boolean isMarkSection) throws IOException {
		if (file == null || !file.exists()) {
			throw new RuntimeException(" file does't exist!");
		}
		List<File> listRes = new ArrayList<File>();
		// 取得每个段的大小
		Vector<Section> lists = splitFileToSection(file, toDir, sectionNum);
		if (lists == null || lists.size() == 0) {
			return listRes;
		}
		if (toDir == null) {
			throw new RuntimeException("toFileDir can't null!");
		}
		if (!toDir.exists()) {
			toDir.mkdirs();
		}
		FileChannel fcin = null;
		try {
			// 源文件流通道
			fcin = new FileInputStream(file).getChannel();
			// 分配（以字节为单位 ）10M //这里分配大小不能超过2G ByteBuffer.allocate(esize)
			// esize为int类型，最大为Integer.MaxSize = 2G = 2 *1024*1024*1024
			int index = 0;
			Section s = lists.get(0);

			listRes.add(s.getDestSectionFile());

			RandomAccessFile rafOut = new RandomAccessFile(s.getDestSectionFile(), "rw");
			if (isMarkSection) { // 需要加入头标识
				rafOut.writeLong(s.getStart());
				rafOut.writeLong(s.getEnd());
				rafOut.seek(16);
			}
			FileChannel fcout = rafOut.getChannel();
			Long splitOneSize = s.getSectionSize();
			if (splitOneSize < bufferSize) {
				bufferSize = Integer.valueOf(String.valueOf(splitOneSize)); // 如果指定的拆分文件大小
																			// 还小于缓冲大小，则指定该值为最小缓冲大小
			}
			ByteBuffer bb = ByteBuffer.allocate(bufferSize);

			long totalSectionSize = 0;
			int everyReadSize = 0;

			while ((everyReadSize = fcin.read(bb)) != -1) {
				// 使缓冲区为一系列新的通道写入或相对获取 操作做好准备：它将限制设置为当前位置，然后将位置设置为 0。
				bb.flip();
				fcout.write(bb);
				bb.clear();
				totalSectionSize += everyReadSize;

				long dif = splitOneSize - totalSectionSize;
				// 一个段最后一个字节组
				if (dif <= bufferSize && dif > 0) {
					bb = ByteBuffer.allocate(Integer.valueOf(String.valueOf(dif)));
				}
				// 一个段读取完成
				if (totalSectionSize == splitOneSize) {
					fcout.force(true);
					fcout.close();
					rafOut.close();

					totalSectionSize = 0;
					index++;
					// 没有超出分割范围
					if (index <= lists.size() - 1) {
						s = lists.get(index);
						listRes.add(s.getDestSectionFile());
						rafOut = new RandomAccessFile(s.getDestSectionFile(), "rw");
						if (isMarkSection) { // 需要加入头标识
							rafOut.writeLong(s.getStart());
							rafOut.writeLong(s.getEnd());
							rafOut.seek(16);
						}
						fcout = rafOut.getChannel();
						splitOneSize = s.getSectionSize();
						bb = ByteBuffer.allocate(bufferSize); // 重新恢复
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (fcin != null) {
				try {
					fcin.close();
				} catch (IOException e) {
				}
			}
		}
		return listRes;
	}

	@Override
	public boolean fileCombine(List<File> listFiles, boolean isMarkSection, File fileDest) {
		if (listFiles == null || listFiles.size() == 0) {
			return false;
		}

		FileOutputStream fout = null;
		FileChannel fcout = null;
		try {
			// 按段的顺序从小到大排序
			if (isMarkSection) {
				Map<Long, File> tmpMap = new TreeMap<Long, File>();
				RandomAccessFile raf = null;
				for (File file : listFiles) {
					try {
						raf = new RandomAccessFile(file, "r");
						long start = raf.readLong();
						// long end = raf.readLong();
						tmpMap.put(start, file);
					} finally {
						if (raf != null) {
							raf.close();
						}
					}
				}
				listFiles = new ArrayList<File>(tmpMap.values());
			} else {
				// 按文件名排序
				Collections.sort(listFiles, new Comparator<File>() {
					@Override
					public int compare(File o1, File o2) {
						String[] names1 = o1.getName().split(SECTION_SPLIT_STUFF);
						String[] names2 = o2.getName().split(SECTION_SPLIT_STUFF);
						int index1 = Integer.parseInt(names1[names1.length - 1]);
						int index2 = Integer.parseInt(names2[names2.length - 1]);
						if (index1 > index2) {
							return 1;
						}
						return 0;
					}
				});
			}

			fout = new FileOutputStream(fileDest);
			fcout = fout.getChannel();
			// 以10M为单位进行一次缓冲
			ByteBuffer bb = ByteBuffer.allocate(getBufferSize());
			RandomAccessFile randomF = null;
			FileChannel fcin = null;
			for (File file : listFiles) {
				try {
					randomF = new RandomAccessFile(file, "r");
					if (isMarkSection) {
						randomF.seek(16);
					}
					fcin = randomF.getChannel();
					while (fcin.read(bb) != -1) {
						bb.flip();
						fcout.write(bb);
						bb.clear();
					}
				} finally {
					fcout.force(true);
					if (fcin != null) {
						fcin.close();
					}
					if (randomF != null) {
						randomF.close();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			if (fcout != null) {
				try {
					fcout.close();
				} catch (IOException e) {
				}
			}
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}
		return true;
	}

	private List<File> loadFile(File sourceDir) {
		if (sourceDir == null) {
			throw new RuntimeException("toFileDir can't null!");
		}
		if (!sourceDir.exists()) {
			throw new RuntimeException("fileDir does't exist!");
		}
		return new ArrayList<File>(FileUtils.listFiles(sourceDir, null, false));
	}

	@Override
	public boolean fileCombile(File sourceDir, boolean isMarkSection, File fileDest)  {
		List<File> sourceFiles = loadFile(sourceDir);
		return fileCombine(sourceFiles, isMarkSection, fileDest);
	}

}
