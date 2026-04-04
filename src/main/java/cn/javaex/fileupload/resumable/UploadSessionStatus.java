package cn.javaex.fileupload.resumable;

/**
 * 上传会话状态枚举。
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public enum UploadSessionStatus {

    /**
     * 已创建。
     */
    CREATED,

    /**
     * 上传中。
     */
    UPLOADING,

    /**
     * 已完成。
     */
    COMPLETED,

    /**
     * 已中止。
     */
    ABORTED
}