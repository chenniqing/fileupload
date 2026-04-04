package cn.javaex.fileupload.resumable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import cn.javaex.fileupload.exception.UploadException;
import cn.javaex.fileupload.util.PathUtils;

/**
 * 简单断点续传服务。
 *
 * <p>功能包括：
 * <br>1. 创建上传会话
 * <br>2. 查询已上传偏移量
 * <br>3. 追加上传分片
 * <br>4. 完成上传
 * <br>5. 中止上传</p>
 *
 * <p>该类是 resumable 核心层，
 * 不直接绑定 Controller、Servlet、Spring MVC。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class SimpleResumableUploadService {

    /**
     * 上传会话仓储。
     */
    private final UploadSessionRepository sessionRepository;

    /**
     * 分片临时存储。
     */
    private final ChunkStore chunkStore;

    /**
     * 创建一个简单断点续传服务。
     *
     * @param sessionRepository 会话仓储
     * @param chunkStore        分片存储
     */
    public SimpleResumableUploadService(UploadSessionRepository sessionRepository, ChunkStore chunkStore) {
        this.sessionRepository = sessionRepository;
        this.chunkStore = chunkStore;
    }

    /**
     * 创建上传会话。
     *
     * @param request 创建会话请求
     * @return 上传会话
     */
    public UploadSession create(CreateUploadSessionRequest request) {
        if (request == null) {
            throw new UploadException("request can not be null");
        }
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            throw new UploadException("fileName can not be empty");
        }
        if (request.getUploadDir() == null || request.getUploadDir().trim().isEmpty()) {
            throw new UploadException("uploadDir can not be empty");
        }

        long now = System.currentTimeMillis();
        UploadSession session = new UploadSession();
        session.setUploadId(UUID.randomUUID().toString().replace("-", ""));
        session.setFileName(request.getFileName());
        session.setUploadDir(request.getUploadDir());
        session.setTotalBytes(request.getTotalBytes());
        session.setUploadedBytes(0L);
        session.setStatus(UploadSessionStatus.CREATED);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);

        return sessionRepository.save(session);
    }

    /**
     * 查询上传会话。
     *
     * @param uploadId 上传会话 ID
     * @return 上传会话
     */
    public UploadSession query(String uploadId) {
        UploadSession session = sessionRepository.find(uploadId);
        if (session == null) {
            throw new UploadException("upload session not found");
        }
        try {
            session.setUploadedBytes(chunkStore.getUploadedBytes(uploadId));
        } catch (IOException e) {
            throw new UploadException("query upload session failed", e);
        }
        return session;
    }

    /**
     * 追加上传分片。
     *
     * @param uploadId    上传会话 ID
     * @param offset      客户端声明的偏移量
     * @param inputStream 分片输入流
     * @param chunkLength 分片长度
     * @return 最新上传会话状态
     */
    public UploadSession append(String uploadId, long offset,
                                InputStream inputStream, long chunkLength) {
        UploadSession session = sessionRepository.find(uploadId);
        if (session == null) {
            throw new UploadException("upload session not found");
        }
        if (session.getStatus() == UploadSessionStatus.COMPLETED) {
            throw new UploadException("upload already completed");
        }
        if (session.getStatus() == UploadSessionStatus.ABORTED) {
            throw new UploadException("upload already aborted");
        }

        try {
            long actualOffset = chunkStore.getUploadedBytes(uploadId);
            if (actualOffset != offset) {
                throw new UploadException("offset mismatch, expected: " + actualOffset + ", actual: " + offset);
            }

            chunkStore.append(uploadId, offset, inputStream, chunkLength);

            long uploaded = chunkStore.getUploadedBytes(uploadId);
            session.setUploadedBytes(uploaded);
            session.setStatus(UploadSessionStatus.UPLOADING);
            session.setUpdatedAt(System.currentTimeMillis());

            return sessionRepository.save(session);
        } catch (IOException e) {
            throw new UploadException("append chunk failed", e);
        }
    }

    /**
     * 完成上传。
     *
     * @param uploadId 上传会话 ID
     * @return 完成后的上传会话
     */
    public UploadSession complete(String uploadId) {
        UploadSession session = sessionRepository.find(uploadId);
        if (session == null) {
            throw new UploadException("upload session not found");
        }

        try {
            long uploaded = chunkStore.getUploadedBytes(uploadId);
            session.setUploadedBytes(uploaded);

            if (session.getTotalBytes() > 0 && uploaded != session.getTotalBytes()) {
                throw new UploadException("upload not complete, uploaded=" + uploaded + ", total=" + session.getTotalBytes());
            }

            File dir = PathUtils.resolvePhysicalDirectory(session.getUploadDir(), false);
            File target = new File(dir, session.getFileName());

            chunkStore.finalizeTo(uploadId, target.getAbsolutePath());

            session.setStatus(UploadSessionStatus.COMPLETED);
            session.setFinalAbsolutePath(target.getAbsolutePath());
            session.setUpdatedAt(System.currentTimeMillis());

            return sessionRepository.save(session);
        } catch (IOException e) {
            throw new UploadException("complete upload failed", e);
        }
    }

    /**
     * 中止上传。
     *
     * @param uploadId 上传会话 ID
     */
    public void abort(String uploadId) {
        UploadSession session = sessionRepository.find(uploadId);
        if (session == null) {
            return;
        }

        try {
            chunkStore.deleteTemp(uploadId);
        } catch (IOException e) {
            throw new UploadException("abort upload failed", e);
        }

        session.setStatus(UploadSessionStatus.ABORTED);
        session.setUpdatedAt(System.currentTimeMillis());
        sessionRepository.save(session);
    }
}