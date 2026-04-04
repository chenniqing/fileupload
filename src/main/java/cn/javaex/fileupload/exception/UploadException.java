package cn.javaex.fileupload.exception;

/**
 * 上传通用异常。
 *
 * <p>本异常作为 fileupload 2.0 工具包的基础运行时异常，
 * 其他上传相关异常可继承自该异常。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class UploadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 创建一个无消息的上传异常。
     */
    public UploadException() {
        super();
    }

    /**
     * 创建一个带消息的上传异常。
     *
     * @param message 异常消息
     */
    public UploadException(String message) {
        super(message);
    }

    /**
     * 创建一个带消息和根异常的上传异常。
     *
     * @param message 异常消息
     * @param cause   根异常
     */
    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 创建一个带根异常的上传异常。
     *
     * @param cause 根异常
     */
    public UploadException(Throwable cause) {
        super(cause);
    }
}