package cn.javaex.fileupload.entity;

import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * 图片设置
 * 
 * @author 陈霓清
 */
public class ImageSetting extends UploadSetting {
	private Integer criticalValue;                          // 图片压缩临界值（单位KB），超过此临界值才会进行压缩处理（非必填）
	private Integer maxWidth;                               // 压缩之后的图片最大宽度（非必填）
	private Integer maxHeight;                              // 压缩之后的图片最大高度（非必填）
	private Position position = Positions.BOTTOM_RIGHT;     // 水印位置（默认为右下角）
	private String watermarkImagePath;                      // 水印图片的绝对路径
	private float opacity = 0.35f;                          // 水印透明度（默认为 0.35）
	private String[] watermarkObjArr = {"big", "small"};    // 水印作用对象（默认大图和小图都添加水印，只有当水印图片的宽高都小于作用对象图片宽高1/3的时候才会添加水印）
	private boolean isDeleteOriginalImage;                  // 是否删除原图
	
	/**
	 * 得到图片压缩临界值（单位KB），超过此临界值才会进行压缩处理
	 * @return
	 */
	public Integer getCriticalValue() {
		return criticalValue;
	}
	/**
	 * 设置图片压缩临界值（单位KB），超过此临界值才会进行压缩处理
	 * @param criticalValue
	 */
	public void setCriticalValue(Integer criticalValue) {
		this.criticalValue = criticalValue;
	}
	
	/**
	 * 得到压缩之后的图片最大宽度
	 * @return
	 */
	public Integer getMaxWidth() {
		return maxWidth;
	}
	/**
	 * 设置压缩之后的图片最大宽度
	 * @param maxWidth
	 */
	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	/**
	 * 得到压缩之后的图片最大高度
	 * @return
	 */
	public Integer getMaxHeight() {
		return maxHeight;
	}
	/**
	 * 设置压缩之后的图片最大高度
	 * @param maxHeight
	 */
	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}
	
	/**
	 * 得到水印位置（默认为右下角）
	 * @return
	 */
	public Position getPosition() {
		return position;
	}
	/**
	 * 设置水印位置（默认为右下角）
	 * @param position
	 */
	public void setPosition(Position position) {
		this.position = position;
	}
	
	/**
	 * 得到水印图片的绝对路径
	 * @return
	 */
	public String getWatermarkImagePath() {
		return watermarkImagePath;
	}
	/**
	 * 设置水印图片的绝对路径
	 * @param imageAbsolutePath
	 */
	public void setWatermarkImagePath(String watermarkImagePath) {
		this.watermarkImagePath = watermarkImagePath;
	}
	
	/**
	 * 得到水印透明度（默认为 0.35）
	 * @return
	 */
	public float getOpacity() {
		return opacity;
	}
	/**
	 * 设置水印透明度（默认为 0.35）
	 * @param opacity
	 */
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}
	
	/**
	 * 得到水印作用对象（默认大图和小图都添加水印，只有当水印图片的宽高都小于作用对象图片宽高1/3的时候才会添加水印）
	 * @return
	 */
	public String[] getWatermarkObjArr() {
		return watermarkObjArr;
	}
	/**
	 * 设置水印作用对象（默认大图和小图都添加水印，只有当水印图片的宽高都小于作用对象图片宽高1/3的时候才会添加水印）
	 * @param watermarkObjArr
	 */
	public void setWatermarkObjArr(String[] watermarkObjArr) {
		this.watermarkObjArr = watermarkObjArr;
	}
	
	/**
	 * 是否删除原图
	 * @return
	 */
	public boolean isDeleteOriginalImage() {
		return isDeleteOriginalImage;
	}
	/**
	 * 设置是否删除原图
	 * @param isDeleteOriginalImage
	 */
	public void setDeleteOriginalImage(boolean isDeleteOriginalImage) {
		this.isDeleteOriginalImage = isDeleteOriginalImage;
	}
	
}
