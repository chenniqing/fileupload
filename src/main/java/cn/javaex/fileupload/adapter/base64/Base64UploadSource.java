package cn.javaex.fileupload.adapter.base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

import cn.javaex.fileupload.core.UploadSource;

/**
 * Base64 上传源实现。
 *
 * <p>支持：
 * <br>1. data:image/png;base64,xxxx
 * <br>2. 纯 base64 内容</p>
 *
 * <p>若传入的是 data URI，会尝试解析 contentType 与默认扩展名。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class Base64UploadSource implements UploadSource {

    /**
     * 解码后的字节数据。
     */
    private final byte[] data;

    /**
     * 原始文件名。
     */
    private final String originalFilename;

    /**
     * 内容类型。
     */
    private final String contentType;

    /**
     * 使用 Base64 文本创建上传源。
     *
     * @param base64DataUri Base64 文本或 data URI
     */
    public Base64UploadSource(String base64DataUri) {
        this(base64DataUri, null);
    }

    /**
     * 使用 Base64 文本和指定文件名创建上传源。
     *
     * @param base64DataUri Base64 文本或 data URI
     * @param fileName      文件名
     */
    public Base64UploadSource(String base64DataUri, String fileName) {
        if (base64DataUri == null || base64DataUri.trim().isEmpty()) {
            throw new IllegalArgumentException("base64 data can not be empty");
        }

        String text = base64DataUri.trim();
        String ct = "application/octet-stream";
        String ext = "bin";
        String payload = text;

        int commaIndex = text.indexOf(',');
        if (text.startsWith("data:") && commaIndex > 0) {
            String meta = text.substring(5, commaIndex);
            payload = text.substring(commaIndex + 1);

            int semicolonIndex = meta.indexOf(';');
            if (semicolonIndex > 0) {
                ct = meta.substring(0, semicolonIndex).trim();
            } else if (!meta.trim().isEmpty()) {
                ct = meta.trim();
            }

            ext = detectExtensionByContentType(ct);
        }

        this.data = Base64.getDecoder().decode(payload);
        this.contentType = ct;
        this.originalFilename = fileName != null && !fileName.trim().isEmpty()
                ? fileName
                : UUID.randomUUID().toString().replace("-", "") + "." + ext;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(data);
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    /**
     * 根据内容类型推断扩展名。
     *
     * @param contentType 内容类型
     * @return 扩展名
     */
    private String detectExtensionByContentType(String contentType) {
        if (contentType == null) {
            return "bin";
        }
        String ct = contentType.toLowerCase();
        if ("image/jpeg".equals(ct)) {
            return "jpg";
        }
        if ("image/png".equals(ct)) {
            return "png";
        }
        if ("image/gif".equals(ct)) {
            return "gif";
        }
        if ("image/bmp".equals(ct)) {
            return "bmp";
        }
        return "bin";
    }
}