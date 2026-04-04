package cn.javaex.fileupload.resumable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于内存的上传会话仓储实现。
 *
 * <p>适用于：
 * <br>1. 单机开发调试
 * <br>2. 本地 demo
 * <br>3. 非生产环境</p>
 *
 * <p>生产环境建议替换为 JDBC / Redis 实现。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class InMemoryUploadSessionRepository implements UploadSessionRepository {

    /**
     * 内存存储容器。
     */
    private final ConcurrentMap<String, UploadSession> store = new ConcurrentHashMap<String, UploadSession>();

    @Override
    public UploadSession save(UploadSession session) {
        store.put(session.getUploadId(), session);
        return session;
    }

    @Override
    public UploadSession find(String uploadId) {
        return store.get(uploadId);
    }

    @Override
    public void delete(String uploadId) {
        store.remove(uploadId);
    }
}