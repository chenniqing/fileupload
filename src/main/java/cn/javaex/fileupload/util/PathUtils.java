package cn.javaex.fileupload.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 路径工具类。
 *
 * <p>用于处理上传目录、日期目录、路径拼接、访问 URL 拼接等工作。</p>
 *
 * <p>本类不依赖 Servlet 请求对象，
 * 因此适合放在 fileupload-core 中长期稳定使用。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public final class PathUtils {

    /**
     * 默认日期目录格式。
     */
    private static final String DATE_PATTERN = "yyyy/MM/dd";

    /**
     * 私有构造方法，禁止实例化。
     */
    private PathUtils() {
    }

    /**
     * 判断路径是否为绝对路径。
     *
     * <p>支持：
     * <br>1. Linux / Unix：/data/upload
     * <br>2. Windows：D:/upload
     * <br>3. UNC：//server/share</p>
     *
     * @param path 路径字符串
     * @return 是否绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (path == null) {
            return false;
        }
        String p = path.trim().replace('\\', '/');
        if (p.isEmpty()) {
            return false;
        }
        if (p.startsWith("/")) {
            return true;
        }
        if (p.startsWith("//")) {
            return true;
        }
        return p.length() >= 2 && Character.isLetter(p.charAt(0)) && p.charAt(1) == ':';
    }

    /**
     * 标准化路径。
     *
     * <p>作用：
     * <br>1. 统一斜杠为 /
     * <br>2. 去掉多余连续 /
     * <br>3. 去掉末尾多余 /
     * <br>4. 保留 Windows 盘符根路径、UNC 路径前缀</p>
     *
     * @param path 原路径
     * @return 标准化后的路径
     */
    public static String normalizePath(String path) {
        if (path == null) {
            return "";
        }
        String p = path.trim().replace('\\', '/');
        boolean unc = p.startsWith("//");
        p = p.replaceAll("/+", "/");
        if (unc && !p.startsWith("//")) {
            p = "/" + p;
        }
        while (p.length() > 1 && p.endsWith("/")) {
            if (p.length() == 3 && Character.isLetter(p.charAt(0)) && p.charAt(1) == ':' && p.charAt(2) == '/') {
                break;
            }
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }

    /**
     * 拼接两个路径。
     *
     * @param parent 父路径
     * @param child  子路径
     * @return 拼接后的路径
     */
    public static String concat(String parent, String child) {
        String p1 = normalizePath(parent);
        String p2 = normalizePath(child);
        if (p1.isEmpty()) {
            return p2;
        }
        if (p2.isEmpty()) {
            return p1;
        }
        if (p1.endsWith("/")) {
            return p1 + (p2.startsWith("/") ? p2.substring(1) : p2);
        }
        return p1 + "/" + (p2.startsWith("/") ? p2.substring(1) : p2);
    }

    /**
     * 获取当前日期目录，例如 2026/03/31。
     *
     * @return 日期目录字符串
     */
    public static String currentDateDirectory() {
        return new SimpleDateFormat(DATE_PATTERN).format(new Date());
    }

    /**
     * 解析物理存储目录。
     *
     * <p>若传入的是相对路径，则以当前项目运行目录 user.dir 为根目录。</p>
     *
     * @param uploadDir      上传目录
     * @param generateByDate 是否自动拼接日期目录
     * @return 物理目录对象
     */
    public static File resolvePhysicalDirectory(String uploadDir, boolean generateByDate) {
        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            throw new IllegalArgumentException("uploadDir can not be null or empty");
        }

        String logicalDir = normalizePath(uploadDir);
        if (generateByDate) {
            logicalDir = concat(logicalDir, currentDateDirectory());
        }

        File dir;
        if (isAbsolutePath(logicalDir)) {
            dir = new File(logicalDir);
        } else {
            dir = new File(System.getProperty("user.dir"), logicalDir);
        }

        if (!dir.exists() && !dir.mkdirs() && !dir.exists()) {
            throw new IllegalStateException("Failed to create directory: " + dir.getAbsolutePath());
        }
        if (!dir.isDirectory()) {
            throw new IllegalStateException("Path exists but is not a directory: " + dir.getAbsolutePath());
        }

        return dir;
    }

    /**
     * 解析逻辑目录。
     *
     * <p>逻辑目录用于返回给业务方，例如：
     * <br>upload/2026/03/31</p>
     *
     * @param uploadDir      上传目录
     * @param generateByDate 是否自动拼接日期目录
     * @return 逻辑目录字符串
     */
    public static String resolveLogicalDirectory(String uploadDir, boolean generateByDate) {
        String logicalDir = normalizePath(uploadDir);
        if (generateByDate) {
            logicalDir = concat(logicalDir, currentDateDirectory());
        }
        return logicalDir;
    }

    /**
     * 构建访问 URL。
     *
     * <p>注意：
     * <br>这里不能复用文件路径的 normalizePath()，
     * 因为 normalizePath() 会压缩连续斜杠，
     * 从而把 http:// 错误处理成 http:/。</p>
     *
     * <p>例如：
     * <br>serverUrlPrefix = http://127.0.0.1:7001
     * <br>logicalFilePath = upload/2026/03/31/test.jpg
     * <br>结果 = http://127.0.0.1:7001/upload/2026/03/31/test.jpg</p>
     *
     * @param serverUrlPrefix 服务访问前缀，例如 http://127.0.0.1:7001
     * @param logicalFilePath 逻辑文件路径，例如 upload/2026/03/31/test.jpg
     * @return 完整访问 URL；若服务前缀为空则返回空字符串
     */
    public static String buildAccessUrl(String serverUrlPrefix, String logicalFilePath) {
        if (serverUrlPrefix == null || serverUrlPrefix.trim().isEmpty()) {
            return "";
        }

        String prefix = serverUrlPrefix.trim().replace('\\', '/');
        String path = logicalFilePath == null ? "" : logicalFilePath.trim().replace('\\', '/');

        // 去掉 prefix 末尾多余 /
        while (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }

        // 去掉 path 开头多余 /
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.isEmpty()) {
            return prefix;
        }

        return prefix + "/" + path;
    }
    
}