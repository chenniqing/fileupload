package cn.javaex.fileupload.verify;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * 图片验证
 * 
 * @author 陈霓清
 */
public class FileTypeVerify {
	
	private static final String[] imgTypeArr = {"jpg", "jpeg", "png", "webp", "bmp", "gif"};
	
	/**
	 * 校验文件类型是否匹配
	 * @param fileName
	 * @param typeArr
	 * @return
	 */
	public static boolean isMatching(String fileName, String[] typeArr) {
		if (fileName==null || fileName.length()==0) {
			return false;
		}
		
		String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		fileSuffix = fileSuffix.toLowerCase();
		
		return Arrays.asList(typeArr).contains(fileSuffix);
	}
	
	/**
	 * 校验是不是一张图片
	 * @param fileName
	 * @return
	 */
	public static boolean isImage(String fileName) {
		if (fileName==null || fileName.length()==0) {
			return false;
		}
		
		String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		fileSuffix = fileSuffix.toLowerCase();
		
		return Arrays.asList(imgTypeArr).contains(fileSuffix);
	}
	
	/**
	 * 校验是不是一张图片
	 * @param file
	 * @return
	 */
	public static boolean isImage(File file) {
		if (file==null) {
			return false;
		}
		
		try {
			Image image = ImageIO.read(file);
			return image != null;
		} catch(IOException ex) {
			return false;
		}
	}
	
}
