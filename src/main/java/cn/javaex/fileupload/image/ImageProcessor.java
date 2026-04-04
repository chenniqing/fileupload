package cn.javaex.fileupload.image;

import java.io.File;
import java.io.IOException;

/**
 * 图片处理器接口。
 *
 * <p>用于抽象图片的缩放、压缩、水印等后处理逻辑。</p>
 *
 * <p>默认实现可提供：
 * <br>1. 纯 JDK 实现
 * <br>2. 后续可扩展更强的 PNG / WebP / AVIF 处理实现</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public interface ImageProcessor {

    /**
     * 处理图片文件。
     *
     * @param originalFile 原图文件
     * @param options      图片处理选项
     * @return 图片处理结果
     * @throws IOException 图片读写失败时抛出
     */
    ImageProcessResult process(File originalFile, ImageOptions options) throws IOException;
}