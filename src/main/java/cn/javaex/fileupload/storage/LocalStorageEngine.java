package cn.javaex.fileupload.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.javaex.fileupload.core.StoredFile;
import cn.javaex.fileupload.core.UploadSource;

/**
 * 本地文件系统存储引擎。
 *
 * <p>将上传数据直接写入本地磁盘目录。</p>
 *
 * <p>适用于：
 * <br>1. 单机部署
 * <br>2. 本地开发
 * <br>3. 文件直接落到项目目录或指定绝对路径</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class LocalStorageEngine implements StorageEngine {

    /**
     * 写文件时使用的缓冲区大小。
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * 将上传源保存为本地文件。
     *
     * @param source    上传源
     * @param directory 保存目录
     * @param fileName  保存文件名
     * @return 已存储文件对象
     * @throws IOException 写入失败时抛出
     */
    @Override
    public StoredFile save(UploadSource source, File directory, String fileName) throws IOException {
        if (!directory.exists() && !directory.mkdirs() && !directory.exists()) {
            throw new IOException("Failed to create directory: " + directory.getAbsolutePath());
        }

        File target = new File(directory, fileName);
        long total = 0L;

        try (InputStream in = source.openStream(); FileOutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                total += len;
            }
            out.flush();
        }

        return new StoredFile(target, total);
    }
}