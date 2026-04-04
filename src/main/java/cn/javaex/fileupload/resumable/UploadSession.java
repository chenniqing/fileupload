package cn.javaex.fileupload.resumable;

/**
 * 上传会话对象。
 *
 * <p>用于描述断点续传的会话状态、已上传字节数、最终文件路径等信息。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class UploadSession {

    /**
     * 上传会话 ID。
     */
    private String uploadId;

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

    /**
     * 已上传字节数。
     */
    private long uploadedBytes;

    /**
     * 会话状态。
     */
    private UploadSessionStatus status;

    /**
     * 创建时间戳。
     */
    private long createdAt;

    /**
     * 最后更新时间戳。
     */
    private long updatedAt;

    /**
     * 最终文件绝对路径。
     */
    private String finalAbsolutePath;

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getUploadedBytes() {
        return uploadedBytes;
    }

    public void setUploadedBytes(long uploadedBytes) {
        this.uploadedBytes = uploadedBytes;
    }

    public UploadSessionStatus getStatus() {
        return status;
    }

    public void setStatus(UploadSessionStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFinalAbsolutePath() {
        return finalAbsolutePath;
    }

    public void setFinalAbsolutePath(String finalAbsolutePath) {
        this.finalAbsolutePath = finalAbsolutePath;
    }
}