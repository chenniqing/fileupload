package cn.javaex.fileupload.resumable;

import java.io.IOException;
import java.io.InputStream;

/**
 * 分片临时存储接口。
 *
 * <p>用于抽象断点续传过程中临时文件的写入、查询和转正逻辑。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public interface ChunkStore {

    /**
     * 从指定偏移量开始追加写入分片数据。
     *
     * @param uploadId    上传会话 ID
     * @param offset      偏移量
     * @param inputStream 分片输入流
     * @param chunkLength 分片长度；小于 0 表示读取到流结束
     * @throws IOException 写入失败时抛出
     */
    void append(String uploadId, long offset, InputStream inputStream, long chunkLength) throws IOException;

    /**
     * 获取当前已上传字节数。
     *
     * @param uploadId 上传会话 ID
     * @return 已上传字节数
     * @throws IOException 查询失败时抛出
     */
    long getUploadedBytes(String uploadId) throws IOException;

    /**
     * 将临时分片文件转成最终文件。
     *
     * @param uploadId           上传会话 ID
     * @param targetAbsolutePath 最终文件绝对路径
     * @throws IOException 转正失败时抛出
     */
    void finalizeTo(String uploadId, String targetAbsolutePath) throws IOException;

    /**
     * 删除临时分片文件。
     *
     * @param uploadId 上传会话 ID
     * @throws IOException 删除失败时抛出
     */
    void deleteTemp(String uploadId) throws IOException;
}