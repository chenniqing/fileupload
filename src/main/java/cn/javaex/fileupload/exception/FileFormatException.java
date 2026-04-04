package cn.javaex.fileupload.exception;

/**
 * 文件格式异常。
 *
 * <p>当文件扩展名、图片真实内容、允许类型等校验不通过时，
 * 建议抛出该异常。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class FileFormatException extends UploadException {

    private static final long serialVersionUID = 1L;

    /**
     * 创建一个无消息的文件格式异常。
     */
    public FileFormatException() {
        super();
    }

    /**
     * 创建一个带消息的文件格式异常。
     *
     * @param message 异常消息
     */
    public FileFormatException(String message) {
        super(message);
    }

    /**
     * 创建一个带消息和根异常的文件格式异常。
     *
     * @param message 异常消息
     * @param cause   根异常
     */
    public FileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 创建一个带根异常的文件格式异常。
     *
     * @param cause 根异常
     */
    public FileFormatException(Throwable cause) {
        super(cause);
    }
}