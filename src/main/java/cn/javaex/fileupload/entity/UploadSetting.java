package cn.javaex.fileupload.entity;

/**
 * 文件上传设置
 * 
 * @author 陈霓清
 */
public class UploadSetting {
	private boolean isGenerateByDate;              // 是否按照日期自动创建3级目录
	private long maxSize;                          // 单个文件的大小上限（单位KB），0为不限制
	private String[] fileTypeArr;                  // 文件类型限制，例如 new String[]{"doc", "docx"}
	
	/**
	 * 是否按照日期自动创建3级目录
	 * @return
	 */
	public boolean isGenerateByDate() {
		return isGenerateByDate;
	}
	/**
	 * 设置是否按照日期自动创建3级目录
	 * @param isGenerateByDate
	 */
	public void setGenerateByDate(boolean isGenerateByDate) {
		this.isGenerateByDate = isGenerateByDate;
	}
	
	/**
	 * 单个文件的大小上限（单位KB），0为不限制
	 * @return
	 */
	public long getMaxSize() {
		return maxSize;
	}
	/**
	 * 设置单个文件的大小上限（单位KB），0为不限制
	 * @param maxSize
	 */
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
	
	/**
	 * 得到文件类型限制，例如 {"doc", "docx"}
	 * @return
	 */
	public String[] getFileTypeArr() {
		return fileTypeArr;
	}
	/**
	 * 设置文件类型限制，例如 {"doc", "docx"}
	 * @param fileTypeArr
	 */
	public void setFileTypeArr(String[] fileTypeArr) {
		this.fileTypeArr = fileTypeArr;
	}
	
}
