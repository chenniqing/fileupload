package cn.javaex.fileupload.storage;

import java.io.File;
import java.io.IOException;

import cn.javaex.fileupload.core.StoredFile;
import cn.javaex.fileupload.core.UploadSource;

/**
 * 存储引擎接口。
 *
 * <p>用于抽象上传源的最终落盘行为。</p>
 *
 * <p>默认可提供：
 * <br>1. 本地存储实现
 * <br>2. 后续扩展 OSS / MinIO / S3 等对象存储实现</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public interface StorageEngine {

    /**
     * 将上传源保存到指定目录和文件名。
     *
     * @param source    上传源
     * @param directory 保存目录
     * @param fileName  保存文件名
     * @return 已存储文件对象
     * @throws IOException 写入失败时抛出
     */
    StoredFile save(UploadSource source, File directory, String fileName) throws IOException;
}