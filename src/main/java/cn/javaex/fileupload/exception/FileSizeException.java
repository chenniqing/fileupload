package cn.javaex.fileupload.exception;

/**
 * 文件大小异常
 * 
 * @author 陈霓清
 */
public class FileSizeException extends UploadException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 构造新的FileSizeException
	 */
	public FileSizeException() {
		super();
	}

	/**
	 * 构造新的FileSizeException
	 *
	 * @param 消息异常的原因
	 */
	public FileSizeException(String message) {
		super(message);
	}

	/**
	 * 构造新的FileSizeException
	 *
	 * @param 导致引发此异常的底层抛出
	 */
	public FileSizeException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造新的FileSizeException
	 *
	 * @param message 消息异常的原因
	 * @param cause   导致引发此异常的底层抛出
	 */
	public FileSizeException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
