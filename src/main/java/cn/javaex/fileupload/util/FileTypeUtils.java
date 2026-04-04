package cn.javaex.fileupload.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * 文件类型工具类。
 *
 * <p>用于处理扩展名提取、扩展名匹配以及真实图片内容识别。</p>
 *
 * <p>注意：
 * <br>1. 扩展名判断只能作为第一层校验；
 * <br>2. 图片上传建议在落盘后再使用 {@link #isImage(File)} 做真实内容校验。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public final class FileTypeUtils {

    /**
     * JDK 默认图片处理推荐支持的扩展名集合。
     *
     * <p>其中 webp 默认不纳入，因为标准 JDK ImageIO 通常不直接支持 webp 写出，
     * 如需支持可后续通过 SPI 扩展。</p>
     */
    private static final Set<String> DEFAULT_IMAGE_EXTENSIONS = new HashSet<String>(
            Arrays.asList("jpg", "jpeg", "png", "bmp", "gif")
    );

    /**
     * 私有构造方法，禁止实例化。
     */
    private FileTypeUtils() {
    }

    /**
     * 获取文件扩展名。
     *
     * @param fileName 文件名
     * @return 小写扩展名；若没有扩展名则返回空字符串
     */
    public static String getExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * 获取去除扩展名后的文件基础名。
     *
     * @param fileName 文件名
     * @return 基础名
     */
    public static String getBaseName(String fileName) {
        if (fileName == null) {
            return "";
        }
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    /**
     * 判断文件扩展名是否匹配允许列表。
     *
     * @param fileName           文件名
     * @param allowedExtensions 允许扩展名数组，例如 {"jpg","png"}
     * @return 是否匹配
     */
    public static boolean matches(String fileName, String[] allowedExtensions) {
        if (allowedExtensions == null || allowedExtensions.length == 0) {
            return true;
        }
        String ext = getExtension(fileName);
        for (String allowed : allowedExtensions) {
            if (allowed != null && ext.equalsIgnoreCase(allowed.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件扩展名是否为默认支持的图片类型。
     *
     * @param fileName 文件名
     * @return 是否为默认支持的图片扩展名
     */
    public static boolean isImageExtension(String fileName) {
        return DEFAULT_IMAGE_EXTENSIONS.contains(getExtension(fileName));
    }

    /**
     * 判断文件真实内容是否为图片。
     *
     * @param file 文件对象
     * @return 是否为图片
     */
    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        try {
            Image image = ImageIO.read(file);
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }
}