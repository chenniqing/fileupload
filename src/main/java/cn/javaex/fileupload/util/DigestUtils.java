package cn.javaex.fileupload.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 摘要工具类。
 *
 * <p>用于对上传文件进行 MD5 / SHA-256 等摘要计算，
 * 便于实现文件命名、文件去重、断点续传完成后的校验等能力。</p>
 *
 * <p>本类不依赖第三方摘要库，直接使用 JDK 标准实现。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public final class DigestUtils {

    /**
     * 读取流时使用的缓冲区大小。
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * 十六进制字符表。
     */
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    /**
     * 私有构造方法，禁止实例化。
     */
    private DigestUtils() {
    }

    /**
     * 计算输入流内容的 MD5，并返回 32 位小写十六进制字符串。
     *
     * @param inputStream 输入流
     * @return MD5 字符串
     * @throws IOException 读取流失败时抛出
     */
    public static String md5Hex(InputStream inputStream) throws IOException {
        return hex(digest("MD5", inputStream));
    }

    /**
     * 计算文件的 MD5，并返回 32 位小写十六进制字符串。
     *
     * @param path 文件路径
     * @return MD5 字符串
     * @throws IOException 读取文件失败时抛出
     */
    public static String md5Hex(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return md5Hex(in);
        }
    }

    /**
     * 计算文件的 SHA-256，并返回小写十六进制字符串。
     *
     * @param path 文件路径
     * @return SHA-256 字符串
     * @throws IOException 读取文件失败时抛出
     */
    public static String sha256Hex(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return hex(digest("SHA-256", in));
        }
    }

    /**
     * 使用指定算法对输入流计算摘要。
     *
     * @param algorithm   摘要算法，例如 MD5、SHA-256
     * @param inputStream 输入流
     * @return 摘要字节数组
     * @throws IOException 读取流失败时抛出
     */
    public static byte[] digest(String algorithm, InputStream inputStream) throws IOException {
        MessageDigest messageDigest = getDigest(algorithm);
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, len);
        }
        return messageDigest.digest();
    }

    /**
     * 获取摘要实例。
     *
     * @param algorithm 摘要算法名称
     * @return MessageDigest 实例
     */
    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将字节数组转换为小写十六进制字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String hex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        int i = 0;
        for (byte b : bytes) {
            chars[i++] = HEX[(b >>> 4) & 0x0F];
            chars[i++] = HEX[b & 0x0F];
        }
        return new String(chars);
    }
}