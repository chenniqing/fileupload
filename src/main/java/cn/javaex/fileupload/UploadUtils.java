package cn.javaex.fileupload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import cn.javaex.fileupload.entity.ImageSetting;
import cn.javaex.fileupload.entity.UploadSetting;
import cn.javaex.fileupload.enums.ImageName;
import cn.javaex.fileupload.exception.FileFormatException;
import cn.javaex.fileupload.exception.FileSizeException;
import cn.javaex.fileupload.exception.UploadException;
import cn.javaex.fileupload.verify.FileTypeVerify;
import net.coobird.thumbnailator.Thumbnails;

/**
 * 文件上传工具类
 * 
 * @author 陈霓清
 */
public class UploadUtils {
	
	/**
	 * 上传文件
	 * @param file
	 * @param uploadPath
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> uploadFile(MultipartFile file, String uploadPath) throws FileFormatException, FileSizeException, UploadException, Exception {
		return uploadFile(file, uploadPath, null);
	}
	
	/**
	 * 上传文件
	 * @param file
	 * @param uploadPath
	 * @param setting
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> uploadFile(MultipartFile file, String uploadPath, UploadSetting setting) throws FileFormatException, FileSizeException, UploadException, Exception {
		return upload(file, uploadPath, setting, "file");
	}
	
	/**
	 * 上传图片
	 * @param file
	 * @param uploadPath
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> uploadImage(MultipartFile file, String uploadPath) throws FileFormatException, FileSizeException, UploadException, Exception {
		return uploadImage(file, uploadPath, null);
	}
	
	/**
	 * 上传图片
	 * @param file
	 * @param uploadPath
	 * @param setting
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> uploadImage(MultipartFile file, String uploadPath, UploadSetting setting) throws FileFormatException, FileSizeException, UploadException, Exception {
		return upload(file, uploadPath, setting, "image");
	}

	/**
	 * 上传
	 * @param file
	 * @param uploadPath
	 * @param setting
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> upload(MultipartFile file, String uploadPath, UploadSetting setting, String type) throws FileFormatException, FileSizeException, UploadException, Exception {
		if (file==null) {
			throw new UploadException("文件不存在");
		}
		
		// 获取文件信息
		String fileName = file.getOriginalFilename();                              // 文件原名称
		if (fileName==null || fileName.length()==0) {
			throw new UploadException("无效的文件名称");
		}
		String fileMd5 = getFileMD5(file);                                         // 文件md5值
		String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);     // 文件后缀
		long fileSize = file.getSize() / 1024;                                     // 文件大小（单位：KB）
		
		// 文件类型校验
		if ("image".equals(type)) {
			if (setting==null || (setting.getFileTypeArr()==null || setting.getFileTypeArr().length==0)) {
				if (!FileTypeVerify.isImage(fileName)) {
					throw new FileFormatException("不支持的文件类型");
				}
			} else {
				if (!FileTypeVerify.isMatching(fileName, setting.getFileTypeArr())) {
					throw new FileFormatException("不支持的文件类型");
				}
			}
		} else {
			if (setting!=null && setting.getFileTypeArr()!=null && setting.getFileTypeArr().length>0) {
				if (!FileTypeVerify.isMatching(fileName, setting.getFileTypeArr())) {
					throw new FileFormatException("不支持的文件类型");
				}
			}
		}
		
		// 校验文件大小
		if (setting!=null && setting.getMaxSize()>0 && fileSize>setting.getMaxSize()) {
			throw new FileSizeException("文件过大，限制：" + setting.getMaxSize() + "KB");
		}
		
		// 路径格式转换
		uploadPath = PathUtils.slashify(uploadPath);
		
		// 按照日期自动生成3级目录
		if (setting!=null && setting.isGenerateByDate()) {
			Date date = new Date();
			// 根据日期自动创建3级目录
			String dateFolder = new SimpleDateFormat("yyyy/MM/dd").format(date);
			// 设置文件存储目录
			uploadPath = uploadPath + "/" + dateFolder;
		}
		
		// 传入的路径是否是绝对路径
		boolean isAbsolutePath = PathUtils.isAbsolutePath(uploadPath);
		
		// 存储文件的物理路径
		String filePath = "";
		if (isAbsolutePath) {
			filePath = uploadPath;
		} else {
			String projectPath = PathUtils.getProjectPath();
			filePath = projectPath + File.separator + uploadPath;
		}
		
		// 自动创建文件夹
		File f = new File(filePath);
		f.mkdirs();
		
		try {
			// 将内存中的数据写入磁盘
			String absolutePath = f.getAbsolutePath();
			File originalFile = new File(absolutePath, fileMd5 + "." + fileSuffix);
			file.transferTo(originalFile);
			
			String fileMd5_watermark = "";
			String smallFileMd5 = "";
			
			// 图片处理
			if ("image".equals(type)) {
				if (setting!=null) {
					if (setting instanceof ImageSetting) {
						// 获取原图的属性
						BufferedImage fromPic = ImageIO.read(new File(absolutePath + "/" + fileMd5 + "." + fileSuffix));
						int width = fromPic.getWidth();
						int height = fromPic.getHeight();
						
						// 分析图片处理的类型
						String settingType = "";
						ImageSetting imageSetting = (ImageSetting) setting;
						if (imageSetting.getCriticalValue()!=null && imageSetting.getMaxWidth()!=null && imageSetting.getMaxHeight()!=null) {
							settingType = "compress";    // 压缩
							smallFileMd5 = fileMd5 + "_" + ImageName.COMPRESS.getValue();
							
							if (imageSetting.getWatermarkImagePath()!=null) {
								settingType = "compress_watermark";    // 压缩 + 水印
								smallFileMd5 = fileMd5 + "_" + ImageName.COMPRESS_WATERMARK.getValue();
							}
							
							// 重新设置图片的宽高
							if (imageSetting.getCriticalValue()<fileSize) {
								width = imageSetting.getMaxWidth();
								height = imageSetting.getMaxHeight();
							}
						} else if (imageSetting.getWatermarkImagePath()!=null) {
							settingType = "watermark";    // 水印
							fileMd5_watermark = fileMd5 + "_" + ImageName.WATERMARK.getValue();
						}
						
						String toPic = "";
						
						switch (settingType) {
							case "compress":
								toPic = absolutePath + "/" + smallFileMd5 + "." + fileSuffix;
								Thumbnails.of(fromPic).size(imageSetting.getMaxWidth(), imageSetting.getMaxHeight()).toFile(toPic);
								break;
							case "compress_watermark":
							case "watermark":
								toPic = absolutePath + "/" + smallFileMd5 + "." + fileSuffix;
								// 水印图片
								BufferedImage watermark = ImageIO.read(new File(imageSetting.getWatermarkImagePath()));
								
								// 判断水印添加对象
								// 大图添加水印
								if (Arrays.asList(imageSetting.getWatermarkObjArr()).contains("big")) {
									fileMd5_watermark = fileMd5 + "_" + ImageName.WATERMARK.getValue();
									String toPic_big = absolutePath + "/" + fileMd5_watermark + "." + fileSuffix;
									// 只有当水印图片比原图的1/3还小时，才添加水印
									if (watermark.getWidth() < fromPic.getWidth()/3 && watermark.getHeight() < fromPic.getHeight()/3) {
										Thumbnails.of(fromPic).size(fromPic.getWidth(), fromPic.getHeight()).watermark(imageSetting.getPosition(), watermark, imageSetting.getOpacity()).toFile(toPic_big);
									}
								}
								// 小图添加水印
								if (imageSetting.getCriticalValue()<fileSize) {
									if (Arrays.asList(imageSetting.getWatermarkObjArr()).contains("small") && "compress_watermark".equals(settingType)) {
										// 只有当水印图片比原图的1/3还小时，才添加水印
										if (watermark.getWidth() < width/3 && watermark.getHeight() < height/3) {
											Thumbnails.of(fromPic).size(width, height).watermark(imageSetting.getPosition(), watermark, imageSetting.getOpacity()).toFile(toPic);
										} else {
											// 否则只做压缩处理
											Thumbnails.of(fromPic).size(width, height).toFile(toPic);
										}
									}
								}
								
								break;
							default:
								break;
						}
						
						// 是否删除原图
						if (imageSetting.isDeleteOriginalImage()) {
							originalFile.delete();
						}
					}
				}
			}
			
			// 上传后的文件路径
			String fileUrl = uploadPath + "/" + fileMd5 + "." + fileSuffix;
			// 原图添加水印后的路径
			String watermarkImageUrl = "";
			if (fileMd5_watermark.length()>0) {
				watermarkImageUrl = uploadPath + "/" + fileMd5_watermark + "." + fileSuffix;
			}
			// 小图的路径
			String smallImageUrl = "";
			if (smallFileMd5.length()>0) {
				smallImageUrl = uploadPath + "/" + smallFileMd5 + "." + fileSuffix;
			}
			
			// 绝对路径
			String fileAbsoluteUrl = fileUrl;
			String watermarkImageAbsoluteUrl = watermarkImageUrl;
			String smallImageAbsoluteUrl = smallImageUrl;
			if (!isAbsolutePath) {
				String serverPath = PathUtils.getServerPath();
				
				fileAbsoluteUrl = serverPath + "/" + uploadPath + "/" + fileMd5 + "." + fileSuffix;
				if (fileMd5_watermark.length()>0) {
					watermarkImageAbsoluteUrl = serverPath + "/" + uploadPath + "/" + fileMd5_watermark + "." + fileSuffix;
				}
				if (smallFileMd5.length()>0) {
					smallImageAbsoluteUrl = serverPath + "/" + uploadPath + "/" + smallFileMd5 + "." + fileSuffix;
				}
			}
			
			// 返回map
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("oldFileName", fileName);                         // 文件原名称
			map.put("newFileName", fileMd5 + "." + fileSuffix);       // 文件MD5+后缀（新名称）
			map.put("fileSize", fileSize);                            // 文件大小（单位KB）
			if (fileSize < 1024) {
				map.put("fileSizeStr", fileSize + " KB");             // 文件大小（带单位）
			} else if (fileSize < 1024*1024) {
				map.put("fileSizeStr", fileSize/1024 + " M");         // 文件大小（带单位）
			} else {
				map.put("fileSizeStr", fileSize/(1024*1024) + " G");  // 文件大小（带单位）
			}
			map.put("fileSuffix", fileSuffix);                        // 文件后缀
			map.put("fileUrl", fileUrl);                              // 文件路径
			map.put("fileAbsoluteUrl", fileAbsoluteUrl);              // 文件绝对路径
			// 图片处理的一些属性
			if (watermarkImageUrl.length()>0 && watermarkImageAbsoluteUrl.length()>0) {
				map.put("watermarkImageUrl", watermarkImageUrl);                    // 原图添加水印后的路径
				map.put("watermarkImageAbsoluteUrl", watermarkImageAbsoluteUrl);    // 原图添加水印后的绝对路径
			}
			if (smallImageUrl.length()>0 && smallImageAbsoluteUrl.length()>0) {
				map.put("smallImageUrl", smallImageUrl);                            // 小图的路径
				map.put("smallImageAbsoluteUrl", smallImageAbsoluteUrl);            // 小图的绝对路径
			}
			
			return map;
		} catch (Exception e) {
			throw new UploadException(e.getMessage());
		}
	}
	
	/**
	 * 获取文件的MD5值
	 * @param file
	 * @return
	 */
	private static String getFileMD5(MultipartFile file) {
		String fileMd5 = "";
		
		CommonsMultipartFile cf = (CommonsMultipartFile) file;
		DiskFileItem fileItem = (DiskFileItem) cf.getFileItem();
		String targetFile = fileItem.getStoreLocation().toString();
		
		try {
			fileMd5 = DigestUtils.md5Hex(new FileInputStream(targetFile));
		} catch (Exception e) {
			fileMd5 = UUID.randomUUID().toString().replace("-", "");
		}
		
		return fileMd5;
	}
}
