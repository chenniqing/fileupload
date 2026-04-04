package cn.javaex.fileupload.resumable;

/**
 * 上传会话仓储接口。
 *
 * <p>用于抽象断点续传会话状态的存取行为。</p>
 *
 * <p>默认可提供：
 * <br>1. 内存实现
 * <br>2. JDBC 实现
 * <br>3. Redis 实现</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public interface UploadSessionRepository {

    /**
     * 保存会话。
     *
     * @param session 上传会话
     * @return 保存后的会话
     */
    UploadSession save(UploadSession session);

    /**
     * 根据上传会话 ID 查询会话。
     *
     * @param uploadId 上传会话 ID
     * @return 上传会话；不存在时返回 null
     */
    UploadSession find(String uploadId);

    /**
     * 删除会话。
     *
     * @param uploadId 上传会话 ID
     */
    void delete(String uploadId);
}