package cn.javaex.fileupload.core;

import java.io.IOException;

import cn.javaex.fileupload.adapter.base64.Base64UploadSource;
import cn.javaex.fileupload.adapter.spring.SpringMultipartUploadSource;
import cn.javaex.fileupload.image.JdkImageProcessor;
import cn.javaex.fileupload.storage.LocalStorageEngine;
import org.springframework.web.multipart.MultipartFile;

/**
 * 2.0 对外静态工具入口。
 *
 * <p>该类主要用于降低业务方接入成本，
 * 让使用习惯更接近 1.x 版本的 UploadUtils。</p>
 *
 * <p>说明：
 * <br>1. 若业务方希望完全自定义存储引擎或图片处理器，
 * 可直接使用 {@link FileUploadFacade}；
 * <br>2. 若只是常规本地上传，可直接使用本工具类。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public final class UploadUtils2 {

    /**
     * 默认上传门面实例。
     */
    private static final FileUploadFacade DEFAULT_FACADE = new FileUploadFacade(
            new LocalStorageEngine(),
            new UploadValidator(),
            new JdkImageProcessor()
    );

    /**
     * 私有构造方法，禁止实例化。
     */
    private UploadUtils2() {
    }

    /**
     * 使用 Spring MultipartFile 上传普通文件。
     *
     * @param file    MultipartFile 对象
     * @param options 上传选项
     * @return 上传结果
     * @throws IOException 文件读写失败时抛出
     */
    public static UploadResult uploadFile(MultipartFile file, UploadOptions options) throws IOException {
        return DEFAULT_FACADE.uploadFile(new SpringMultipartUploadSource(file), options);
    }

    /**
     * 使用 Spring MultipartFile 上传图片。
     *
     * @param file    MultipartFile 对象
     * @param options 上传选项
     * @return 上传结果
     * @throws IOException 文件读写失败时抛出
     */
    public static UploadResult uploadImage(MultipartFile file, UploadOptions options) throws IOException {
        return DEFAULT_FACADE.uploadImage(new SpringMultipartUploadSource(file), options);
    }

    /**
     * 使用 Base64 上传普通文件。
     *
     * <p>通常适用于 data URI 或前端直接传来的 base64 内容。</p>
     *
     * @param base64  Base64 文本
     * @param options 上传选项
     * @return 上传结果
     * @throws IOException 文件读写失败时抛出
     */
    public static UploadResult uploadBase64(String base64, UploadOptions options) throws IOException {
        return DEFAULT_FACADE.uploadFile(new Base64UploadSource(base64), options);
    }

    /**
     * 使用 Base64 上传图片。
     *
     * @param base64  Base64 文本
     * @param options 上传选项
     * @return 上传结果
     * @throws IOException 文件读写失败时抛出
     */
    public static UploadResult uploadBase64Image(String base64, UploadOptions options) throws IOException {
        return DEFAULT_FACADE.uploadImage(new Base64UploadSource(base64), options);
    }
}