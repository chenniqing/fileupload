package cn.javaex.fileupload.core;

import java.io.File;

/**
 * 已存储文件对象。
 *
 * <p>表示上传源已经被落盘后的中间结果。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class StoredFile {

    /**
     * 已落盘文件对象。
     */
    private final File file;

    /**
     * 文件大小，单位字节。
     */
    private final long size;

    /**
     * 创建一个已存储文件对象。
     *
     * @param file 文件对象
     * @param size 文件大小，单位字节
     */
    public StoredFile(File file, long size) {
        this.file = file;
        this.size = size;
    }

    /**
     * 获取文件对象。
     *
     * @return 文件对象
     */
    public File getFile() {
        return file;
    }

    /**
     * 获取文件大小，单位字节。
     *
     * @return 文件大小
     */
    public long getSize() {
        return size;
    }
}