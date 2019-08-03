package com.yirun.framework.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/**
 * @Description : SFTP工具类
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.utils.SftpUtils.java
 * @Author : yanbinghuang
 */
public class SftpUtils {
	private static Logger logger = Logger.getLogger(SftpUtils.class);

	private ChannelSftp sftp = null;

	private static SftpUtils sftpUtils = null;
	// SFTP地址
	private String host = "";
	// SFTP用户名
	private String userName = "";
	// SFTP密码
	private String password = "";
	// 端口号 默认22
	private int port = 22;

	public static SftpUtils getSftpUtils() {
		return sftpUtils;
	}

	public static void setSftpUtils(SftpUtils sftpUtils) {
		SftpUtils.sftpUtils = sftpUtils;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public SftpUtils(String host, int port, String userName, String password) {
		this.host = host;
		this.userName = userName;
		this.password = password;
		this.port = port;

	}

	/**
	 * @Description :获取工具类实例
	 * @Method_Name : getInstance;
	 * @param host
	 *            主机IP
	 * @param port
	 *            端口号
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * @return : SftpUtils;
	 * @Creation Date : 2017年8月11日 下午2:52:52;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public static synchronized SftpUtils getInstance(String host, int port, String userName, String password) {
		if (sftpUtils == null) {
			sftpUtils = new SftpUtils(host, port, userName, password);
		}
		return sftpUtils;
	}

	/***
	 * @Description : 获取ChannelSftp
	 * @Method_Name : getSftp;
	 * @return
	 * @return : ChannelSftp;
	 * @Creation Date : 2017年8月11日 上午10:59:17;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public ChannelSftp getSftp() {
		// 创建JSch对象
		JSch jsch = new JSch();
		Channel channel = null;
		try {
			// 根据用户名，主机IP,端口获取一个Session对象
			Session sshSession = jsch.getSession(this.userName, this.host, this.port);
			// 设置密码
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			// 为Session对象设置 properties
			sshSession.setConfig(sshConfig);
			// 设置timeout时间
			sshSession.connect(20000);
			// 打开SFTP通道
			channel = sshSession.openChannel("sftp");
			// 建立SFTP通道连接
			channel.connect();
		} catch (JSchException e) {
			e.printStackTrace();
			logger.info("getSftp exception：", e);
		}
		return (ChannelSftp) channel;
	}

	/**
	 * @Description :关闭SFTP连接
	 * @Method_Name : closeChannelConnect;
	 * @return : void;
	 * @Creation Date : 2017年8月11日 上午11:01:08;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public void closeChannelConnect() {
		try {
			if (sftp != null) {
				if (sftp.isConnected()) {
					sftp.disconnect();
				} else if (sftp.isClosed()) {
					logger.info("Session close...........");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description :下载单个文件到指定目录
	 * @Method_Name : downloadFile;
	 * @param remotePath
	 *            远程下载目录(以路径符号结束)
	 * @param remoteFileName
	 *            下载文件名
	 * @param localPath
	 *            本地保存目录(以路径符号结束)
	 * @param localFileName
	 *            保存文件名
	 * @return
	 * @return : boolean;
	 * @throws Exception
	 * @Creation Date : 2017年8月11日 上午11:31:39;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public File downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
		FileOutputStream fileOutPut = null;
		try {
			sftp = sftpUtils.getSftp();
			File file = mkdirs(localPath + localFileName);
			fileOutPut = new FileOutputStream(file);
			sftp.get(remotePath + remoteFileName, fileOutPut);
			if (logger.isInfoEnabled()) {
				logger.info("DownloadFile:" + remoteFileName + " success from sftp.");
			}
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fileOutPut) {
				try {
					fileOutPut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			closeChannelConnect();
		}
		return null;
	}

	/**
	 * @Description : 批量下载文件
	 * @Method_Name : batchDownLoadFile;
	 * @param remotePath
	 *            远程下载目录(以路径符号结束,可以为相对路径eg:/assess/sftp/jiesuan_2/2014/)
	 * @param localPath
	 *            本地保存目录(以路径符号结束,D:\Duansha\sftp\)
	 * @param fileFormat
	 *            下载文件格式(以特定字符开头,为空不做检验)
	 * @param fileEndFormat
	 *            下载文件格式(文件格式)
	 * @param del
	 *            下载后是否删除sftp文件
	 * @return
	 * @return : List<String>;
	 * @Creation Date : 2017年8月11日 下午12:23:20;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public List<String> batchDownLoadFile(String remotePath, String localPath, String fileFormat, String fileEndFormat,
			boolean del) {
		List<String> filenames = new ArrayList<String>();
		try {
			sftp = sftpUtils.getSftp();
			Vector v = listFiles(remotePath);
			if (v.size() > 0) {
				System.out.println("本次处理文件个数不为零,开始下载...fileSize=" + v.size());
				Iterator it = v.iterator();
				while (it.hasNext()) {
					LsEntry entry = (LsEntry) it.next();
					String filename = entry.getFilename();
					SftpATTRS attrs = entry.getAttrs();
					if (!attrs.isDir()) {
						File flag = null;
						String localFileName = localPath + filename;
						fileFormat = fileFormat == null ? "" : fileFormat.trim();
						fileEndFormat = fileEndFormat == null ? "" : fileEndFormat.trim();
						// 三种情况
						if (fileFormat.length() > 0 && fileEndFormat.length() > 0) {
							if (filename.startsWith(fileFormat) && filename.endsWith(fileEndFormat)) {
								flag = downloadFile(remotePath, filename, localPath, filename);
								if (flag != null) {
									filenames.add(localFileName);
									if (flag != null && del) {
										deleteSFTP(remotePath, filename);
									}
								}
							}
						} else if (fileFormat.length() > 0 && "".equals(fileEndFormat)) {
							if (filename.startsWith(fileFormat)) {
								flag = downloadFile(remotePath, filename, localPath, filename);
								if (flag != null) {
									filenames.add(localFileName);
									if (flag != null && del) {
										deleteSFTP(remotePath, filename);
									}
								}
							}
						} else if (fileEndFormat.length() > 0 && "".equals(fileFormat)) {
							if (filename.endsWith(fileEndFormat)) {
								flag = downloadFile(remotePath, filename, localPath, filename);
								if (flag != null) {
									filenames.add(localFileName);
									if (flag != null && del) {
										deleteSFTP(remotePath, filename);
									}
								}
							}
						} else {
							flag = downloadFile(remotePath, filename, localPath, filename);
							if (flag != null) {
								filenames.add(localFileName);
								if (flag != null && del) {
									deleteSFTP(remotePath, filename);
								}
							}
						}
					}
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("download file is success:remotePath=" + remotePath + "and localPath=" + localPath
						+ ",file size is" + v.size());
			}
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeChannelConnect();
		}
		return filenames;
	}

	/**
	 * @Description : 上传单个文件
	 * @Method_Name : uploadFile;
	 * @param remotePath
	 *            远程保存目录
	 * @param remoteFileName
	 *            保存文件名
	 * @param localPath
	 *            本地上传目录(以路径符号结束)
	 * @param localFileName
	 *            上传的文件名
	 * @return
	 * @return : boolean;
	 * @Creation Date : 2017年8月11日 上午11:40:36;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public boolean uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
		FileInputStream in = null;
		try {
			sftp = sftpUtils.getSftp();
			createDir(remotePath);
			File file = new File(localPath + localFileName);
			in = new FileInputStream(file);
			sftp.put(in, remoteFileName);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			closeChannelConnect();
		}
		return false;
	}

	/**
	 * @Description : 批量上传文件
	 * @Method_Name : bacthUploadFile;
	 * @param remotePath
	 *            远程保存目录
	 * @param localPath
	 *            本地上传目录(以路径符号结束)
	 * @param del
	 *            true 删除 false不删除 上传后是否删除本地文件
	 * @return
	 * @return : boolean;
	 * @Creation Date : 2017年8月11日 上午11:52:29;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public boolean bacthUploadFile(String remotePath, String localPath, boolean del) {
		try {
			sftp = sftpUtils.getSftp();
			File file = new File(localPath);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile() && files[i].getName().indexOf("bak") == -1) {
					if (this.uploadFile(remotePath, files[i].getName(), localPath, files[i].getName()) && del) {
						deleteFile(localPath + files[i].getName());
					}
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("upload file is success:remotePath=" + remotePath + "and localPath=" + localPath
						+ ",file size is " + files.length);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeChannelConnect();
		}
		return false;

	}

	/**
	 * @Description : 创建目录
	 * @Method_Name : createDir;
	 * @param createpath
	 * @return
	 * @return : boolean;
	 * @Creation Date : 2017年8月11日 上午11:42:33;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public boolean createDir(String createpath) {
		try {
			if (isDirExist(createpath)) {
				sftp.cd(createpath);
				return true;
			}
			String pathArry[] = createpath.split("/");
			StringBuffer filePath = new StringBuffer("/");
			for (String path : pathArry) {
				if (path.equals("")) {
					continue;
				}
				filePath.append(path + "/");
				if (isDirExist(filePath.toString())) {
					sftp.cd(filePath.toString());
				} else {
					// 建立目录
					sftp.mkdir(filePath.toString());
					// 进入并设置为当前目录
					sftp.cd(filePath.toString());
				}

			}
			sftp.cd(createpath);
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @Description : 判断目录是否存在
	 * @Method_Name : isDirExist;
	 * @param directory
	 * @return
	 * @return : boolean;
	 * @Creation Date : 2017年8月11日 上午11:42:20;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public boolean isDirExist(String directory) {
		boolean isDirExistFlag = false;
		try {
			SftpATTRS sftpATTRS = sftp.lstat(directory);
			isDirExistFlag = true;
			return sftpATTRS.isDir();
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().equals("no such file")) {
				isDirExistFlag = false;
			}
		}
		return isDirExistFlag;
	}

	/**
	 * @Description : 如果目录不存在就创建目录
	 * @Method_Name : mkdirs;
	 * @param path
	 * @return : File;
	 * @throws IOException
	 * @Creation Date : 2017年8月11日 上午11:43:10;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public static File mkdirs(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			file.createNewFile();
		}
		return file;
	}

	/**
	 * @Description : 删除本地文件
	 * @Method_Name : deleteFile;
	 * @param filePath
	 * @return
	 * @return : boolean;
	 * @Creation Date : 2017年8月11日 上午11:47:52;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		}
		if (!file.isFile()) {
			return false;
		}
		boolean rs = file.delete();
		if (rs && logger.isInfoEnabled()) {
			logger.info("delete file success from local.");
		}
		return rs;
	}

	/**
	 * 
	 * @Description : 删除stfp文件
	 * @Method_Name : deleteSFTP;
	 * @param directory
	 *            要删除文件所在目录
	 * @param deleteFile
	 *            要删除的文件
	 * @return : void;
	 * @Creation Date : 2017年8月11日 上午11:58:04;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public void deleteSFTP(String directory, String deleteFile) {
		try {
			sftp.rm(directory + deleteFile);
			if (logger.isInfoEnabled()) {
				logger.info("delete file success from sftp.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description : 列出目录下的文件
	 * @Method_Name : listFiles;
	 * @param directory
	 *            要列出的目录
	 * @return
	 * @throws SftpException
	 * @return : Vector;
	 * @Creation Date : 2017年8月11日 上午11:59:00;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	public Vector listFiles(String directory) throws SftpException {
		return sftp.ls(directory);
	}

	public static void main(String[] args) throws Exception {
		SftpUtils sftpUtils = SftpUtils.getInstance("hz-sftp1.lianlianpay.com", 2122, "bj-yircf", "FC9UdnE9");
		String remotePath = "/bj-yircf/201504281000302505/";
		String remoteFileName = "JYMX_201504281000302505_20170605.txt";
		String localPath = "D:\\lianlian_reconciliation/";
		String localFileName = "JYMX_201504281000302505_20170605.txt";
		sftpUtils.downloadFile(remotePath, remoteFileName, localPath, localFileName);

	}
}
