package cn.javaex.fileupload.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * 上传源抽象接口。
 *
 * <p>该接口是 fileupload 2.0 的核心输入模型，
 * 用于统一描述“一个可上传的数据来源”。</p>
 *
 * <p>它可以适配：
 * <br>1. Spring MultipartFile
 * <br>2. javax.servlet.http.Part
 * <br>3. jakarta.servlet.http.Part
 * <br>4. Base64 数据
 * <br>5. 本地文件、网络流等其他自定义来源</p>
 *
 * <p>这样设计的目的是：让 core 不直接依赖 Spring / Servlet。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public interface UploadSource {

    /**
     * 获取原始文件名。
     *
     * @return 原始文件名，例如 test.jpg
     */
    String getOriginalFilename();

    /**
     * 获取文件大小，单位字节。
     *
     * @return 文件大小
     */
    long getSize();

    /**
     * 打开输入流。
     *
     * @return 输入流
     * @throws IOException 打开流失败时抛出
     */
    InputStream openStream() throws IOException;

    /**
     * 获取内容类型。
     *
     * <p>例如 image/jpeg、image/png、application/pdf。</p>
     *
     * @return 内容类型；未知时可返回 null
     */
    default String getContentType() {
        return null;
    }
}