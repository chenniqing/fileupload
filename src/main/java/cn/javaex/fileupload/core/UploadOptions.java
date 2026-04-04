package cn.javaex.fileupload.core;

import cn.javaex.fileupload.image.ImageOptions;

/**
 * 上传选项。
 *
 * <p>用于定义上传目录、大小限制、允许扩展名、服务访问前缀以及图片处理策略等。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class UploadOptions {

    /**
     * 上传目录。
     *
     * <p>支持相对路径或绝对路径。</p>
     */
    private String uploadDir;

    /**
     * 是否自动按日期生成三级目录。
     *
     * <p>例如 upload/2026/03/31。</p>
     */
    private boolean generateByDate;

    /**
     * 单个文件大小上限，单位 KB。
     *
     * <p>小于等于 0 表示不限制。</p>
     */
    private long maxSizeKb;

    /**
     * 允许的扩展名数组。
     *
     * <p>例如 {"jpg","png"}。</p>
     */
    private String[] allowedExtensions;

    /**
     * 服务访问前缀。
     *
     * <p>例如 http://127.0.0.1:8080。</p>
     */
    private String serverUrlPrefix;

    /**
     * 图片处理选项。
     *
     * <p>仅在上传图片时生效。</p>
     */
    private ImageOptions imageOptions;

    /**
     * 获取上传目录。
     *
     * @return 上传目录
     */
    public String getUploadDir() {
        return uploadDir;
    }

    /**
     * 设置上传目录。
     *
     * @param uploadDir 上传目录
     * @return 当前对象
     */
    public UploadOptions setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
        return this;
    }

    /**
     * 获取是否自动按日期创建目录。
     *
     * @return 是否自动按日期创建目录
     */
    public boolean isGenerateByDate() {
        return generateByDate;
    }

    /**
     * 设置是否自动按日期创建目录。
     *
     * @param generateByDate 是否自动按日期创建目录
     * @return 当前对象
     */
    public UploadOptions setGenerateByDate(boolean generateByDate) {
        this.generateByDate = generateByDate;
        return this;
    }

    /**
     * 获取单个文件大小上限，单位 KB。
     *
     * @return 文件大小上限
     */
    public long getMaxSizeKb() {
        return maxSizeKb;
    }

    /**
     * 设置单个文件大小上限，单位 KB。
     *
     * @param maxSizeKb 文件大小上限
     * @return 当前对象
     */
    public UploadOptions setMaxSizeKb(long maxSizeKb) {
        this.maxSizeKb = maxSizeKb;
        return this;
    }

    /**
     * 获取允许扩展名数组。
     *
     * @return 允许扩展名数组
     */
    public String[] getAllowedExtensions() {
        return allowedExtensions;
    }

    /**
     * 设置允许扩展名数组。
     *
     * @param allowedExtensions 允许扩展名数组
     * @return 当前对象
     */
    public UploadOptions setAllowedExtensions(String[] allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
        return this;
    }

    /**
     * 获取服务访问前缀。
     *
     * @return 服务访问前缀
     */
    public String getServerUrlPrefix() {
        return serverUrlPrefix;
    }

    /**
     * 设置服务访问前缀。
     *
     * @param serverUrlPrefix 服务访问前缀
     * @return 当前对象
     */
    public UploadOptions setServerUrlPrefix(String serverUrlPrefix) {
        this.serverUrlPrefix = serverUrlPrefix;
        return this;
    }

    /**
     * 获取图片处理选项。
     *
     * @return 图片处理选项
     */
    public ImageOptions getImageOptions() {
        return imageOptions;
    }

    /**
     * 设置图片处理选项。
     *
     * @param imageOptions 图片处理选项
     * @return 当前对象
     */
    public UploadOptions setImageOptions(ImageOptions imageOptions) {
        this.imageOptions = imageOptions;
        return this;
    }
}