package cn.javaex.fileupload.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Servlet 响应兼容工具。
 *
 * <p>用于同时兼容以下两种类型：</p>
 * <ul>
 *     <li>javax.servlet.http.HttpServletResponse</li>
 *     <li>jakarta.servlet.http.HttpServletResponse</li>
 * </ul>
 *
 * <p>本类不在编译期直接依赖 javax.servlet-api 或 jakarta.servlet-api，
 * 而是通过反射调用公共方法，从而保证主包可以做到零 Servlet API 依赖。</p>
 *
 * <p>适用场景：</p>
 * <ul>
 *     <li>文件下载</li>
 *     <li>图片输出到浏览器</li>
 *     <li>自定义 header 设置</li>
 *     <li>断点续传协议输出</li>
 * </ul>
 *
 * <p>调用方可以直接把 response 参数传为：</p>
 * <ul>
 *     <li>javax.servlet.http.HttpServletResponse</li>
 *     <li>jakarta.servlet.http.HttpServletResponse</li>
 * </ul>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public final class ServletResponseCompat {

    /**
     * 私有构造方法，禁止实例化。
     */
    private ServletResponseCompat() {
    }

    /**
     * 设置响应头。
     *
     * @param response 响应对象
     * @param name     响应头名称
     * @param value    响应头值
     */
    public static void setHeader(Object response, String name, String value) {
        invoke(response, "setHeader",
                new Class<?>[]{String.class, String.class},
                new Object[]{name, value});
    }

    /**
     * 添加响应头。
     *
     * @param response 响应对象
     * @param name     响应头名称
     * @param value    响应头值
     */
    public static void addHeader(Object response, String name, String value) {
        invoke(response, "addHeader",
                new Class<?>[]{String.class, String.class},
                new Object[]{name, value});
    }

    /**
     * 设置日期响应头。
     *
     * @param response 响应对象
     * @param name     响应头名称
     * @param value    时间戳
     */
    public static void setDateHeader(Object response, String name, long value) {
        invoke(response, "setDateHeader",
                new Class<?>[]{String.class, long.class},
                new Object[]{name, value});
    }

    /**
     * 设置整数响应头。
     *
     * @param response 响应对象
     * @param name     响应头名称
     * @param value    整数值
     */
    public static void setIntHeader(Object response, String name, int value) {
        invoke(response, "setIntHeader",
                new Class<?>[]{String.class, int.class},
                new Object[]{name, value});
    }

    /**
     * 设置内容类型。
     *
     * @param response    响应对象
     * @param contentType 内容类型
     */
    public static void setContentType(Object response, String contentType) {
        invoke(response, "setContentType",
                new Class<?>[]{String.class},
                new Object[]{contentType});
    }

    /**
     * 设置字符编码。
     *
     * @param response 响应对象
     * @param charset  字符编码
     */
    public static void setCharacterEncoding(Object response, String charset) {
        invoke(response, "setCharacterEncoding",
                new Class<?>[]{String.class},
                new Object[]{charset});
    }

    /**
     * 设置 HTTP 状态码。
     *
     * @param response 响应对象
     * @param status   状态码
     */
    public static void setStatus(Object response, int status) {
        invoke(response, "setStatus",
                new Class<?>[]{int.class},
                new Object[]{status});
    }

    /**
     * 发送错误状态码。
     *
     * @param response 响应对象
     * @param status   状态码
     * @throws IOException 发送失败时抛出
     */
    public static void sendError(Object response, int status) throws IOException {
        invokeChecked(response, "sendError",
                new Class<?>[]{int.class},
                new Object[]{status});
    }

    /**
     * 发送错误状态码和错误消息。
     *
     * @param response 响应对象
     * @param status   状态码
     * @param message  错误消息
     * @throws IOException 发送失败时抛出
     */
    public static void sendError(Object response, int status, String message) throws IOException {
        invokeChecked(response, "sendError",
                new Class<?>[]{int.class, String.class},
                new Object[]{status, message});
    }

    /**
     * 设置内容长度（int 版本）。
     *
     * @param response 响应对象
     * @param length   内容长度
     */
    public static void setContentLength(Object response, int length) {
        invoke(response, "setContentLength",
                new Class<?>[]{int.class},
                new Object[]{length});
    }

    /**
     * 设置内容长度（long 版本）。
     *
     * <p>如果当前运行环境不支持该方法，且 length 在 int 范围内，
     * 会自动回退到 setContentLength(int)。</p>
     *
     * @param response 响应对象
     * @param length   内容长度
     */
    public static void setContentLengthLong(Object response, long length) {
        Object result = invokeQuietly(response, "setContentLengthLong",
                new Class<?>[]{long.class},
                new Object[]{length});

        if (result == null && length <= Integer.MAX_VALUE && length >= Integer.MIN_VALUE) {
            setContentLength(response, (int) length);
        }
    }

    /**
     * 获取输出流。
     *
     * <p>javax/jakarta 的 ServletOutputStream 都继承自 OutputStream，
     * 因此这里可安全向上转型。</p>
     *
     * @param response 响应对象
     * @return 输出流
     */
    public static OutputStream getOutputStream(Object response) {
        Object value = invoke(response, "getOutputStream", new Class<?>[]{}, new Object[]{});
        if (!(value instanceof OutputStream)) {
            throw new IllegalStateException("response.getOutputStream() did not return an OutputStream");
        }
        return (OutputStream) value;
    }

    /**
     * 获取字符输出流。
     *
     * @param response 响应对象
     * @return Writer
     */
    public static Writer getWriter(Object response) {
        Object value = invoke(response, "getWriter", new Class<?>[]{}, new Object[]{});
        if (!(value instanceof Writer)) {
            throw new IllegalStateException("response.getWriter() did not return a Writer");
        }
        return (Writer) value;
    }

    /**
     * 刷新响应缓冲区。
     *
     * @param response 响应对象
     */
    public static void flushBuffer(Object response) {
        invoke(response, "flushBuffer", new Class<?>[]{}, new Object[]{});
    }

    /**
     * 重置响应对象。
     *
     * @param response 响应对象
     */
    public static void reset(Object response) {
        invoke(response, "reset", new Class<?>[]{}, new Object[]{});
    }

    /**
     * 判断响应是否已经提交。
     *
     * @param response 响应对象
     * @return 是否已提交
     */
    public static boolean isCommitted(Object response) {
        Object value = invoke(response, "isCommitted", new Class<?>[]{}, new Object[]{});
        return value instanceof Boolean && (Boolean) value;
    }

    /**
     * 设置下载响应头。
     *
     * @param response 响应对象
     * @param fileName 下载文件名
     */
    public static void setDownloadHeader(Object response, String fileName) {
        try {
            String safeName = fileName == null ? "download" : fileName;
            String encoded = URLEncoder.encode(safeName, StandardCharsets.UTF_8.name()).replace("+", "%20");
            setHeader(response, "Content-Disposition",
                    "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode download file name", e);
        }
    }

    /**
     * 准备一个标准二进制下载响应。
     *
     * @param response    响应对象
     * @param fileName    下载文件名
     * @param contentType 内容类型；为空时默认 application/octet-stream
     * @param contentSize 内容大小，单位字节；小于 0 表示不设置
     */
    public static void prepareBinaryDownload(Object response, String fileName,
                                             String contentType, long contentSize) {
        reset(response);
        setCharacterEncoding(response, StandardCharsets.UTF_8.name());
        setContentType(response, isBlank(contentType) ? "application/octet-stream" : contentType);
        setDownloadHeader(response, fileName);
        if (contentSize >= 0) {
            setContentLengthLong(response, contentSize);
        }
    }

    /**
     * 向响应输出字节数组。
     *
     * @param response 响应对象
     * @param bytes    字节数组
     * @throws IOException 写出失败时抛出
     */
    public static void writeBytes(Object response, byte[] bytes) throws IOException {
        OutputStream out = getOutputStream(response);
        out.write(bytes);
        out.flush();
    }

    /**
     * 向响应输出文本。
     *
     * @param response 响应对象
     * @param text     文本内容
     * @param charset  字符编码；为空时默认 UTF-8
     * @throws IOException 写出失败时抛出
     */
    public static void writeText(Object response, String text, String charset) throws IOException {
        String actualCharset = isBlank(charset) ? StandardCharsets.UTF_8.name() : charset;
        OutputStream out = getOutputStream(response);
        Writer writer = new OutputStreamWriter(out, actualCharset);
        writer.write(text == null ? "" : text);
        writer.flush();
    }

    /**
     * 调用指定方法。
     *
     * @param target         目标对象
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param args           参数值
     * @return 返回值
     */
    private static Object invoke(Object target, String methodName,
                                 Class<?>[] parameterTypes, Object[] args) {
        if (target == null) {
            throw new IllegalArgumentException("response can not be null");
        }

        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to invoke method: " + methodName, e);
        }
    }

    /**
     * 调用指定方法，并将 IOException 原样抛出。
     *
     * @param target         目标对象
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param args           参数值
     * @return 返回值
     * @throws IOException 底层抛出 IOException 时原样抛出
     */
    private static Object invokeChecked(Object target, String methodName,
                                        Class<?>[] parameterTypes, Object[] args) throws IOException {
        if (target == null) {
            throw new IllegalArgumentException("response can not be null");
        }

        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new IllegalStateException("Failed to invoke method: " + methodName, e);
        }
    }

    /**
     * 安静调用方法。
     *
     * <p>失败时不抛异常，直接返回 null。</p>
     *
     * @param target         目标对象
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param args           参数值
     * @return 返回值；失败时返回 null
     */
    private static Object invokeQuietly(Object target, String methodName,
                                        Class<?>[] parameterTypes, Object[] args) {
        if (target == null) {
            return null;
        }

        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断字符串是否为空白。
     *
     * @param text 文本
     * @return 是否为空白
     */
    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}