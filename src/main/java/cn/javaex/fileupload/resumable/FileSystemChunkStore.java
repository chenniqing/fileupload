package cn.javaex.fileupload.resumable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * 基于本地文件系统的分片临时存储实现。
 *
 * <p>每个上传会话对应一个 .part 临时文件，
 * 通过 RandomAccessFile 按偏移量写入。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class FileSystemChunkStore implements ChunkStore {

    /**
     * 临时目录根路径。
     */
    private final File tempRoot;

    /**
     * 写文件缓冲区大小。
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * 创建一个本地文件系统分片存储。
     *
     * @param tempRootPath 临时目录根路径
     */
    public FileSystemChunkStore(String tempRootPath) {
        this.tempRoot = new File(tempRootPath);
        if (!tempRoot.exists() && !tempRoot.mkdirs() && !tempRoot.exists()) {
            throw new IllegalStateException("Failed to create temp root: " + tempRoot.getAbsolutePath());
        }
    }

    @Override
    public void append(String uploadId, long offset, InputStream inputStream, long chunkLength) throws IOException {
        File tempFile = tempFile(uploadId);
        if (!tempFile.exists()) {
            File parent = tempFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs() && !parent.exists()) {
                throw new IOException("Failed to create temp directory: " + parent.getAbsolutePath());
            }
            tempFile.createNewFile();
        }

        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.seek(offset);

            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = chunkLength;
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                if (chunkLength >= 0) {
                    if (remaining <= 0) {
                        break;
                    }
                    int writeLen = (int) Math.min(len, remaining);
                    raf.write(buffer, 0, writeLen);
                    remaining -= writeLen;
                } else {
                    raf.write(buffer, 0, len);
                }
            }
        }
    }

    @Override
    public long getUploadedBytes(String uploadId) throws IOException {
        File tempFile = tempFile(uploadId);
        return tempFile.exists() ? tempFile.length() : 0L;
    }

    @Override
    public void finalizeTo(String uploadId, String targetAbsolutePath) throws IOException {
        File tempFile = tempFile(uploadId);
        if (!tempFile.exists()) {
            throw new IOException("temp upload file not found");
        }

        File target = new File(targetAbsolutePath);
        File parent = target.getParentFile();
        if (!parent.exists() && !parent.mkdirs() && !parent.exists()) {
            throw new IOException("Failed to create target directory: " + parent.getAbsolutePath());
        }

        Files.move(tempFile.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void deleteTemp(String uploadId) throws IOException {
        File tempFile = tempFile(uploadId);
        if (tempFile.exists() && !tempFile.delete()) {
            throw new IOException("Failed to delete temp file: " + tempFile.getAbsolutePath());
        }
    }

    /**
     * 根据 uploadId 获取对应的临时分片文件。
     *
     * @param uploadId 上传会话 ID
     * @return 临时文件对象
     */
    private File tempFile(String uploadId) {
        return new File(tempRoot, uploadId + ".part");
    }
}