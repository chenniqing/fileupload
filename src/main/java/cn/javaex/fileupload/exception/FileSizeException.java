package cn.javaex.fileupload.exception;

/**
 * 文件大小异常。
 *
 * <p>当上传文件大小超过限制时，建议抛出该异常。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class FileSizeException extends UploadException {

    private static final long serialVersionUID = 1L;

    /**
     * 创建一个无消息的文件大小异常。
     */
    public FileSizeException() {
        super();
    }

    /**
     * 创建一个带消息的文件大小异常。
     *
     * @param message 异常消息
     */
    public FileSizeException(String message) {
        super(message);
    }

    /**
     * 创建一个带消息和根异常的文件大小异常。
     *
     * @param message 异常消息
     * @param cause   根异常
     */
    public FileSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 创建一个带根异常的文件大小异常。
     *
     * @param cause 根异常
     */
    public FileSizeException(Throwable cause) {
        super(cause);
    }
}