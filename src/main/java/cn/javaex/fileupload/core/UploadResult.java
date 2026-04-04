package cn.javaex.fileupload.core;

/**
 * 上传结果对象。
 *
 * <p>用于统一返回上传后的文件信息、逻辑路径、绝对路径、访问 URL 以及图片衍生文件路径。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class UploadResult {

    /**
     * 原始文件名。
     */
    private String oldFileName;

    /**
     * 新文件名。
     */
    private String newFileName;

    /**
     * 文件大小，单位 KB。
     */
    private long fileSizeKb;

    /**
     * 文件大小字符串，例如 123 KB、3 MB。
     */
    private String fileSizeStr;

    /**
     * 文件扩展名。
     */
    private String fileSuffix;

    /**
     * 逻辑文件路径。
     */
    private String filePath;

    /**
     * 绝对物理路径。
     */
    private String absolutePath;

    /**
     * 访问 URL。
     */
    private String accessUrl;

    /**
     * 原图水印文件逻辑路径。
     */
    private String watermarkFilePath;

    /**
     * 原图水印文件绝对路径。
     */
    private String watermarkAbsolutePath;

    /**
     * 原图水印文件访问 URL。
     */
    private String watermarkAccessUrl;

    /**
     * 小图文件逻辑路径。
     */
    private String smallFilePath;

    /**
     * 小图文件绝对路径。
     */
    private String smallAbsolutePath;

    /**
     * 小图文件访问 URL。
     */
    private String smallAccessUrl;

    public String getOldFileName() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public long getFileSizeKb() {
        return fileSizeKb;
    }

    public void setFileSizeKb(long fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }

    public String getFileSizeStr() {
        return fileSizeStr;
    }

    public void setFileSizeStr(String fileSizeStr) {
        this.fileSizeStr = fileSizeStr;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getWatermarkFilePath() {
        return watermarkFilePath;
    }

    public void setWatermarkFilePath(String watermarkFilePath) {
        this.watermarkFilePath = watermarkFilePath;
    }

    public String getWatermarkAbsolutePath() {
        return watermarkAbsolutePath;
    }

    public void setWatermarkAbsolutePath(String watermarkAbsolutePath) {
        this.watermarkAbsolutePath = watermarkAbsolutePath;
    }

    public String getWatermarkAccessUrl() {
        return watermarkAccessUrl;
    }

    public void setWatermarkAccessUrl(String watermarkAccessUrl) {
        this.watermarkAccessUrl = watermarkAccessUrl;
    }

    public String getSmallFilePath() {
        return smallFilePath;
    }

    public void setSmallFilePath(String smallFilePath) {
        this.smallFilePath = smallFilePath;
    }

    public String getSmallAbsolutePath() {
        return smallAbsolutePath;
    }

    public void setSmallAbsolutePath(String smallAbsolutePath) {
        this.smallAbsolutePath = smallAbsolutePath;
    }

    public String getSmallAccessUrl() {
        return smallAccessUrl;
    }

    public void setSmallAccessUrl(String smallAccessUrl) {
        this.smallAccessUrl = smallAccessUrl;
    }
}