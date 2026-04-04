package cn.javaex.fileupload.adapter.spring;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import cn.javaex.fileupload.core.UploadSource;

/**
 * Spring MultipartFile 上传源适配器。
 *
 * <p>用于将 Spring MVC / Spring Boot 中的 MultipartFile
 * 适配为 fileupload-core 可识别的 UploadSource。</p>
 *
 * <p>说明：
 * <br>1. Spring Boot 2 和 Spring Boot 3 的 MultipartFile 包名仍然一致；
 * <br>2. 因此该适配器可以继续沿用同一套代码。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class SpringMultipartUploadSource implements UploadSource {

    /**
     * Spring 上传文件对象。
     */
    private final MultipartFile multipartFile;

    /**
     * 创建一个 Spring MultipartFile 适配器。
     *
     * @param multipartFile MultipartFile 对象
     */
    public SpringMultipartUploadSource(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public String getOriginalFilename() {
        return multipartFile.getOriginalFilename();
    }

    @Override
    public long getSize() {
        return multipartFile.getSize();
    }

    @Override
    public InputStream openStream() throws IOException {
        return multipartFile.getInputStream();
    }

    @Override
    public String getContentType() {
        return multipartFile.getContentType();
    }
}