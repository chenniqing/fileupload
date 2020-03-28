package cn.javaex.fileupload.exception;

/**
 * 上传异常
 * 
 * @author 陈霓清
 */
public class UploadException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 构造新的UploadException
	 */
	public UploadException() {
		super();
	}

	/**
	 * 构造新的UploadException
	 *
	 * @param 消息异常的原因
	 */
	public UploadException(String message) {
		super(message);
	}

	/**
	 * 构造新的UploadException
	 *
	 * @param 导致引发此异常的底层抛出
	 */
	public UploadException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构造新的UploadException
	 *
	 * @param message 消息异常的原因
	 * @param cause   导致引发此异常的底层抛出
	 */
	public UploadException(String message, Throwable cause) {
		super(message, cause);
	}

}
