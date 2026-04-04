package cn.javaex.fileupload.resumable;

/**
 * 创建上传会话请求。
 *
 * <p>用于断点续传初始化时传入文件名、目标目录和总大小等信息。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class CreateUploadSessionRequest {

    /**
     * 文件名。
     */
    private String fileName;

    /**
     * 上传目录。
     */
    private String uploadDir;

    /**
     * 文件总字节数。
     */
    private long totalBytes;

    public String getFileName() {
        return fileName;
    }

    public CreateUploadSessionRequest setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public CreateUploadSessionRequest setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
        return this;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public CreateUploadSessionRequest setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
        return this;
    }
}