package cn.javaex.fileupload.enums;

/**
 * 图片名称
 * 
 * @author 陈霓清
 */
public enum ImageName {
	WATERMARK("0", "0"),
	COMPRESS("1", "1"),
	COMPRESS_WATERMARK("2", "2")
	;
	
	private String value;
	private String text;
	
	private ImageName(String value, String text) {
		this.value = value;
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
