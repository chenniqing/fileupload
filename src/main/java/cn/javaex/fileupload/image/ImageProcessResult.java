package cn.javaex.fileupload.image;

import java.io.File;

/**
 * 图片处理结果。
 *
 * <p>用于描述原图、水印图、小图等输出结果。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class ImageProcessResult {

    /**
     * 原图文件。
     */
    private File originalFile;

    /**
     * 原图水印文件。
     */
    private File watermarkFile;

    /**
     * 压缩/缩放后的小图文件。
     */
    private File smallFile;

    /**
     * 是否已删除原图。
     */
    private boolean originalDeleted;

    public File getOriginalFile() {
        return originalFile;
    }

    public void setOriginalFile(File originalFile) {
        this.originalFile = originalFile;
    }

    public File getWatermarkFile() {
        return watermarkFile;
    }

    public void setWatermarkFile(File watermarkFile) {
        this.watermarkFile = watermarkFile;
    }

    public File getSmallFile() {
        return smallFile;
    }

    public void setSmallFile(File smallFile) {
        this.smallFile = smallFile;
    }

    public boolean isOriginalDeleted() {
        return originalDeleted;
    }

    public void setOriginalDeleted(boolean originalDeleted) {
        this.originalDeleted = originalDeleted;
    }
}