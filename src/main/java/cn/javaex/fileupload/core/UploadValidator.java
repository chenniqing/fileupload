package cn.javaex.fileupload.core;

import cn.javaex.fileupload.exception.FileFormatException;
import cn.javaex.fileupload.exception.FileSizeException;
import cn.javaex.fileupload.exception.UploadException;
import cn.javaex.fileupload.util.FileTypeUtils;

/**
 * 上传校验器。
 *
 * <p>用于进行文件名、文件大小、扩展名等基础校验。</p>
 *
 * <p>建议上传图片时采用“两层校验”：
 * <br>1. 先校验扩展名；
 * <br>2. 再在落盘后校验真实图片内容。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class UploadValidator {

    /**
     * 执行基础校验。
     *
     * @param source  上传源
     * @param options 上传选项
     */
    public void validateBasic(UploadSource source, UploadOptions options) {
        if (source == null) {
            throw new UploadException("upload source can not be null");
        }

        String fileName = source.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new UploadException("invalid file name");
        }

        long sizeKb = source.getSize() <= 0 ? 0 : (source.getSize() + 1023) / 1024;
        if (options != null && options.getMaxSizeKb() > 0 && sizeKb > options.getMaxSizeKb()) {
            throw new FileSizeException("file too large, max: " + options.getMaxSizeKb() + "KB");
        }

        if (options != null && options.getAllowedExtensions() != null && options.getAllowedExtensions().length > 0) {
            if (!FileTypeUtils.matches(fileName, options.getAllowedExtensions())) {
                throw new FileFormatException("unsupported file type");
            }
        }
    }

    /**
     * 校验文件名是否为默认支持的图片扩展名。
     *
     * @param fileName 文件名
     */
    public void validateImageFileName(String fileName) {
        if (!FileTypeUtils.isImageExtension(fileName)) {
            throw new FileFormatException("unsupported image type for JDK processor");
        }
    }
}