/*
 * Copyright 2012 Timothy Lin <lzh9102@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yirun.framework.core.img;

import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.render.ReadRender;
import com.alibaba.simpleimage.render.ScaleParameter;
import com.alibaba.simpleimage.render.ScaleRender;
import com.alibaba.simpleimage.render.WriteRender;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class ImageHelper {
	
	private static final int BLACK = 0xff000000;
	private static final int WHITE = 0xffffffff;
	
	private ImageHelper() {}
	
	public static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int x=0; x<width; ++x) {
			for (int y=0; y<height; ++y) {
				image.setRGB(x, y, matrix.get(x,y) ? BLACK : WHITE);
			}
		}
		return image;
	}
	
	public static BufferedImage toBufferedImage(BitMatrix matrix,File logo) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int x=0; x<width; ++x) {
			for (int y=0; y<height; ++y) {
				image.setRGB(x, y, matrix.get(x,y) ? BLACK : WHITE);
			}
		}
		// add logo
	  	if(logo.exists()){
    		BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(logo);
				if(bufferedImage != null){
					ScaleImage scaleImage = new ScaleImage();
					int w = width/5 > bufferedImage.getWidth() ? bufferedImage.getWidth() : width/5;
					int h = height/5 > bufferedImage.getWidth() ? bufferedImage.getWidth() : height/5;
					int[] array = new int[w*h];
					bufferedImage = scaleImage.imageZoomOut(bufferedImage, w, h);
					array = bufferedImage.getRGB(0, 0, w, h, array, 0, w);
					
					int x = width/2 - w/2;
					int y = height/2 - h/2;
					if(x > 0 && y > 0){
						image.setRGB(x, y, w, h, array, 0, w);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
    	}
		
		return image;
	}
	
	public static BufferedImage cropImage(BufferedImage src, int x, int y, int w, int h)
	{
	    BufferedImage dest = new BufferedImage(w, h, src.getType());
	    Graphics g = dest.getGraphics();
	    g.drawImage(src, 0, 0, w, h, x, y, x+w, y+h, null);
	    g.dispose();
	    return dest;
	}
	
	public static BufferedImage resizeImage(BufferedImage src, int w, int h) {
		BufferedImage dest = new BufferedImage(w, h, src.getType());
		Graphics g = dest.getGraphics();
		g.drawImage(src, 0, 0, w, h, null);
		g.dispose();
		return dest;
	}


	/**
	 * 使用阿里的插件进行图片压缩
	 * @param in
	 * @param out
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static void aliResizeImage(File in,File out,int maxWidth,int maxHeight){
		//将图像缩略到1024x1024以内，不足1024x1024则不做任何处理
		ScaleParameter scaleParam = new ScaleParameter(maxWidth, maxHeight);
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		WriteRender wr = null;
		try {
			inStream = new FileInputStream(in);
			outStream = new FileOutputStream(out);
			ImageRender rr = new ReadRender(inStream);
			ImageRender sr = new ScaleRender(rr, scaleParam);
			wr = new WriteRender(sr, outStream);
			//触发图像处理
			wr.render();
		} catch(Exception e) {
			//skip
		} finally {
			//图片文件输入输出流必须记得关闭
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(outStream);
			if (wr != null) {
				try {
					//释放simpleImage的内部资源
					wr.dispose();
				} catch (SimpleImageException ignore) {
					// skip ...
				}
			}
		}
	}
}
	

