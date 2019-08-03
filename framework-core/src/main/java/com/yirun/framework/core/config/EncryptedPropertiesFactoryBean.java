//package com.yirun.framework.core.config;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.core.io.Resource;
//import org.springframework.util.Assert;
//
//import com.yirun.framework.core.exception.GeneralException;
//
//public class EncryptedPropertiesFactoryBean implements FactoryBean<Properties[]> {
//	
//	byte[] encryptedPrefixFlag = {53, -81, -123, -93, -55, 66, 107, -4, 55, -51, -66, -39, 44, -100, -108, -24,
//			                      -24,-108,-100,  44, -39,-66, -51, 55, -4, 107,  66, -55,-93, -123, -81,  53};
//	
//	byte[] encryptedSuffixFlag = {76,   4, 84, -72, -1, -105, 82, -15, 76, -80,   4, 75, -54, 41, -70, 125,
//			                      125,-70, 41, -54, 75,    4,-80,  76,-15,  82,-105, -1, -72, 84,   4, 76 };
//	
//	private int keySize = 128;
//	
//	private List<Properties> propertiesList = new ArrayList<Properties>();
//	
//	private Resource[] locations;
//	
//	public EncryptedPropertiesFactoryBean() {
//		
//	}
//	
//	public EncryptedPropertiesFactoryBean(Resource[] locations) {
//		setLocations(locations);
//	}
//	
//	public void setLocations(Resource[] locations) {
//		this.locations = locations;
//		//立刻加载
//		load();
//	}
//	
//	private void load() {
//		Assert.notEmpty(locations," the locations is null");
//		try {
//			for (Resource location : locations) {
//				if(!location.exists() || !location.isReadable()) {
//					return;
//				}
//				byte[] content = null;
//				try {
//					RandomAccessFile accessFile = new RandomAccessFile(location.getFile(), "r");
//					content = new byte[(int) accessFile.length()];
//					accessFile.readFully(content);
//					accessFile.close();
//				} catch (FileNotFoundException exception) {
//					throw exception;
//				} catch (IOException exception) {
//					throw exception;
//				}
//				
//				if(content == null || content.length < 1) {
//					return;
//				}
//				Properties properties = new Properties();
//				if(isEncrypted(content)) {
//					byte[] decryptedContent = decrypt(content);
//					properties.load(new ByteArrayInputStream(decryptedContent));
//				} else {
//					byte[] encryptedContent = encrypt(content);
//					String orignalFilePath = location.getFile().getAbsolutePath();
//					String tempFilePath = location.getFile().getAbsolutePath() + new Date().getTime();
//					File tempFile = new File(tempFilePath);
//					if(tempFile.createNewFile()) {
//						RandomAccessFile encryptedFile = new RandomAccessFile(tempFile, "rw");
//						encryptedFile.write(encryptedContent);
//						encryptedFile.close();
//						if(!location.getFile().delete() || !tempFile.renameTo(new File(orignalFilePath))) {
//							throw new IOException( "rename encrypted file to " + orignalFilePath + " error ");
//						}
//					}
//					properties.load(new ByteArrayInputStream(content));
//				}		
//				propertiesList.add(properties);
//			}
//		} catch (Exception e) {
//			throw new GeneralException("load EncryptedProperties error", e);
//		}
//	}
//	
//	private boolean isEncrypted(byte[] content) {
//		if(content == null || content.length < encryptedPrefixFlag.length + encryptedSuffixFlag.length + keySize/8) {
//			return false;
//		}
//		boolean isEncrypted = true;
//		//比较前缀
//		for(int i = 0 ; i < encryptedPrefixFlag.length; i++) {
//			if(encryptedPrefixFlag[i] != content[i]) {
//				isEncrypted = false;
//				break;
//			}
//		}
//		//比较后缀
//		for(int i = content.length - encryptedSuffixFlag.length, j = 0; i < content.length; i++, j++) {
//			if(encryptedSuffixFlag[j] != content[i]) {
//				isEncrypted = false;
//				break;
//			}
//		}
//		return isEncrypted;
//	}
//	
//	private byte[] decrypt(byte[] content) throws Exception{
//		byte[] keyBytes = new byte[keySize/8];
//		//如果已经加密则紧接在前缀后面的keySize/8个字节就是key
//		for(int i = 0, j = encryptedPrefixFlag.length ; j < encryptedPrefixFlag.length + keyBytes.length; i++, j++) {
//			keyBytes[i] = content[j];
//		}
//		byte[] encryptedContent = Arrays.copyOfRange(content, encryptedPrefixFlag.length + keyBytes.length, content.length - encryptedSuffixFlag.length);
//		SecretKey key = new SecretKeySpec(keyBytes,"AES");
//		Cipher cipher = Cipher.getInstance("AES"); // 创建密码器
//		cipher.init(Cipher.DECRYPT_MODE, key); // 初始化
//		return cipher.doFinal(encryptedContent);
//	}
//	
//	private byte[] encrypt(byte[] originalContent) throws Exception{
//		//==========================先加密后写入文件============================
//		//产生key
//		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//		//共128位16个字节
//		keyGenerator.init(keySize); 
//		SecretKey key = keyGenerator.generateKey();
//		byte[] keyBytes = key.getEncoded();
//		//加密原文
//		Cipher cipher = Cipher.getInstance("AES"); // 创建密码器
//		cipher.init(Cipher.ENCRYPT_MODE, key); // 初始化
//		byte[] encryptedOriginalContent = cipher.doFinal(originalContent);
//		
//		//==========================写入文件============================		
//		byte[] encryptedContent = new byte[encryptedPrefixFlag.length + keyBytes.length + encryptedOriginalContent.length + encryptedSuffixFlag.length];
//		//添加前缀
//		for (int i = 0; i < encryptedPrefixFlag.length; i++) {
//			encryptedContent[i] = encryptedPrefixFlag[i];
//		}
//		//写入key
//		for (int i = encryptedPrefixFlag.length, j = 0; i < encryptedPrefixFlag.length + keyBytes.length; i++, j++) {
//			encryptedContent[i] = keyBytes[j];
//		}
//		//写入加密后的密文
//		for (int i = encryptedPrefixFlag.length + keyBytes.length, j = 0; i < encryptedPrefixFlag.length + keyBytes.length + encryptedOriginalContent.length; i++, j++) {
//			encryptedContent[i] = encryptedOriginalContent[j];
//		}
//		//写入后缀
//		for (int i = encryptedPrefixFlag.length + keyBytes.length + encryptedOriginalContent.length, j = 0; i < encryptedPrefixFlag.length + keyBytes.length + encryptedOriginalContent.length + encryptedSuffixFlag.length; i++, j++) {
//			encryptedContent[i] = encryptedSuffixFlag[j];
//		}
//		//返回变换后的密文
//		return encryptedContent;
//	}
//	
//	public Properties[] getObject() throws Exception {
//		return propertiesList.toArray(new Properties[propertiesList.size()]);
//	}
//
//	public Class<?> getObjectType() {
//		return Properties.class;
//	}
//
//	public boolean isSingleton() {
//		return true;
//	}
//
//}
