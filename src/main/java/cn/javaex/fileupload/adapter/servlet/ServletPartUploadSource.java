package cn.javaex.fileupload.adapter.servlet;

import cn.javaex.fileupload.core.UploadSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Servlet Part 通用上传源适配器。
 *
 * <p>用于同时兼容以下两种类型：</p>
 * <ul>
 *     <li>javax.servlet.http.Part</li>
 *     <li>jakarta.servlet.http.Part</li>
 * </ul>
 *
 * <p>本类不在编译期直接依赖 javax.servlet-api 或 jakarta.servlet-api，
 * 而是通过反射调用公共方法，从而保证主包可以做到零 Servlet API 依赖。</p>
 *
 * <p>适用场景：</p>
 * <ul>
 *     <li>Spring Boot 2（javax.servlet）</li>
 *     <li>Spring Boot 3+（jakarta.servlet）</li>
 *     <li>其他基于 Servlet Multipart 的项目</li>
 * </ul>
 *
 * <p>支持调用的核心方法：</p>
 * <ul>
 *     <li>getSubmittedFileName()</li>
 *     <li>getInputStream()</li>
 *     <li>getSize()</li>
 *     <li>getContentType()</li>
 * </ul>
 *
 * <p>如果运行环境中的 Part 对象没有 getSubmittedFileName()，
 * 会尝试通过 content-disposition 请求头解析文件名。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class ServletPartUploadSource implements UploadSource {

    /**
     * 实际传入的 Part 对象。
     *
     * <p>可能是 javax.servlet.http.Part，也可能是 jakarta.servlet.http.Part。</p>
     */
    private final Object part;

    /**
     * 缓存的原始文件名。
     */
    private final String originalFilename;

    /**
     * 使用通用 Part 对象创建上传源。
     *
     * @param part Servlet Part 对象，支持 javax/jakarta 两种实现
     */
    public ServletPartUploadSource(Object part) {
        if (part == null) {
            throw new IllegalArgumentException("part can not be null");
        }
        this.part = part;
        this.originalFilename = resolveOriginalFilename(part);
    }

    /**
     * 获取原始文件名。
     *
     * @return 原始文件名；若无法解析则返回 unknown
     */
    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    /**
     * 获取文件大小，单位字节。
     *
     * @return 文件大小
     */
    @Override
    public long getSize() {
        Object value = invoke(part, "getSize");
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 打开输入流。
     *
     * @return 输入流
     * @throws IOException 打开失败时抛出
     */
    @Override
    public InputStream openStream() throws IOException {
        Object value = invoke(part, "getInputStream");
        if (!(value instanceof InputStream)) {
            throw new IOException("part.getInputStream() did not return an InputStream");
        }
        return (InputStream) value;
    }

    /**
     * 获取内容类型。
     *
     * @return 内容类型；未知时返回 null
     */
    @Override
    public String getContentType() {
        Object value = invoke(part, "getContentType");
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 获取底层原始 Part 对象。
     *
     * <p>当业务方确实需要访问底层对象时可使用该方法。</p>
     *
     * @return 原始 Part 对象
     */
    public Object getNativePart() {
        return part;
    }

    /**
     * 解析原始文件名。
     *
     * <p>优先尝试调用 Part.getSubmittedFileName()；
     * 若当前运行环境不存在该方法，则尝试解析 content-disposition 头。</p>
     *
     * @param part Part 对象
     * @return 文件名
     */
    private String resolveOriginalFilename(Object part) {
        // 优先走 getSubmittedFileName()
        Object fileNameValue = invokeQuietly(part, "getSubmittedFileName");
        if (fileNameValue != null) {
            String fileName = String.valueOf(fileNameValue).trim();
            if (!fileName.isEmpty()) {
                return fileName;
            }
        }

        // 回退：从 content-disposition 中解析
        Object headerValue = invokeQuietly(part, "getHeader", new Class<?>[]{String.class}, new Object[]{"content-disposition"});
        if (headerValue != null) {
            String fileName = parseFileNameFromContentDisposition(String.valueOf(headerValue));
            if (fileName != null && !fileName.trim().isEmpty()) {
                return fileName;
            }
        }

        return "unknown";
    }

    /**
     * 从 content-disposition 请求头中解析文件名。
     *
     * @param header content-disposition 头
     * @return 文件名；解析失败时返回 null
     */
    private String parseFileNameFromContentDisposition(String header) {
        if (header == null || header.trim().isEmpty()) {
            return null;
        }

        String[] parts = header.split(";");
        for (String item : parts) {
            String token = item.trim();
            if (token.startsWith("filename=")) {
                String fileName = token.substring("filename=".length()).trim();
                if (fileName.startsWith("\"") && fileName.endsWith("\"") && fileName.length() >= 2) {
                    fileName = fileName.substring(1, fileName.length() - 1);
                }

                // 兼容 IE/老浏览器可能上传完整路径
                fileName = fileName.replace("\\", "/");
                int slashIndex = fileName.lastIndexOf('/');
                if (slashIndex >= 0 && slashIndex < fileName.length() - 1) {
                    fileName = fileName.substring(slashIndex + 1);
                }
                return fileName;
            }
        }

        return null;
    }

    /**
     * 调用无参方法。
     *
     * @param target     目标对象
     * @param methodName 方法名
     * @return 调用返回值
     */
    private Object invoke(Object target, String methodName) {
        return invoke(target, methodName, new Class<?>[]{}, new Object[]{});
    }

    /**
     * 调用指定方法。
     *
     * @param target         目标对象
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param args           参数值
     * @return 调用返回值
     */
    private Object invoke(Object target, String methodName, Class<?>[] parameterTypes, Object[] args) {
        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to invoke method: " + methodName, e);
        }
    }

    /**
     * 安静调用方法。
     *
     * <p>调用失败时不抛异常，直接返回 null，
     * 适用于兼容性探测或降级逻辑。</p>
     *
     * @param target     目标对象
     * @param methodName 方法名
     * @return 调用返回值；失败时返回 null
     */
    private Object invokeQuietly(Object target, String methodName) {
        return invokeQuietly(target, methodName, new Class<?>[]{}, new Object[]{});
    }

    /**
     * 安静调用指定方法。
     *
     * @param target         目标对象
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param args           参数值
     * @return 调用返回值；失败时返回 null
     */
    private Object invokeQuietly(Object target, String methodName, Class<?>[] parameterTypes, Object[] args) {
        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            return null;
        }
    }
}