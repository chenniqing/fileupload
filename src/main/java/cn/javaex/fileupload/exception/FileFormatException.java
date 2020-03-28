package cn.javaex.fileupload.exception;

/**
 * 文件格式异常
 * 
 * @author 陈霓清
 */
public class FileFormatException extends UploadException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 构造新的FileFormatException
	 */
	public FileFormatException() {
		super();
	}

	/**
	 * 构造新的FileFormatException
	 *
	 * @param 消息异常的原因
	 */
	public FileFormatException(String message) {
		super(message);
	}

	/**
	 * 构造新的FileFormatException
	 *
	 * @param 导致引发此异常的底层抛出
	 */
	public FileFormatException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造新的FileFormatException
	 *
	 * @param message 消息异常的原因
	 * @param cause   导致引发此异常的底层抛出
	 */
	public FileFormatException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
