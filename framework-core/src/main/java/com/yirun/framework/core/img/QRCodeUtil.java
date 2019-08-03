package com.yirun.framework.core.img;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 二维码生成和解析工具 
 * @author liudh
 * @date 2015-11-19
 ***/

public class QRCodeUtil {

	
	private static QRCodeUtil instance;
	private static QRCodeWriter writer = new QRCodeWriter();
	private static QRCodeReader reader = new QRCodeReader();
	
	//字符类型
	private static final int ENCODE_UTF8=0;
	private static final int ENCODE_ISO=1;
	private static final int ENCODE_JIS=2;
	
	
	private QRCodeUtil(){}
	
	public static synchronized  QRCodeUtil getInstance(){
		if(QRCodeUtil.instance==null){
			QRCodeUtil.instance=new QRCodeUtil();
		}
		return QRCodeUtil.instance;
	}
	
	private static final String[] encodings = {
		"UTF-8", "ISO-8859-1", "Shift_JIS"
	};

	/**
	 * 生成二维码图片
	 * @param url URL连接
	 * @param size 图片尺寸 默认128*128
	 * @return 生成的二维码图片文件
	 * */
	public File encodeURL(String url,int size,String fileName) throws WriterException, IOException {
		
		//建立编码信息
		Hashtable<EncodeHintType, String> options = new Hashtable<>();
		options.put(EncodeHintType.CHARACTER_SET, encodings[ENCODE_UTF8]);
		File retFile = new File(fileName);
		//尝试写入图片
		BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, size, size, options);
		BufferedImage image = ImageHelper.toBufferedImage(matrix);
		ImageIO.write(image, "png", retFile);
		//返回生成的文件
		return retFile;
	}


}
