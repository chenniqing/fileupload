package cn.javaex.fileupload.image;

/**
 * 图片处理选项。
 *
 * <p>用于定义图片压缩、缩放、水印、删除原图等行为。</p>
 *
 * <p>该类是 2.0 对 1.x ImageSetting 的替代模型，
 * 不再依赖 Thumbnailator 的 Position 类型。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class ImageOptions {

    /**
     * 触发压缩的临界大小，单位 KB。
     *
     * <p>大于该大小时才会执行压缩或缩放；
     * 小于等于 0 或 null 时，表示只要配置了缩放参数就可以处理。</p>
     */
    private Integer criticalSizeKb;

    /**
     * 压缩后最大宽度。
     */
    private Integer maxWidth;

    /**
     * 压缩后最大高度。
     */
    private Integer maxHeight;

    /**
     * 缩放比例。
     *
     * <p>取值范围建议为 0 ~ 1.0，优先级高于 maxWidth / maxHeight。</p>
     */
    private Double scale;

    /**
     * 输出质量。
     *
     * <p>仅 JPEG 有明确质量控制意义，
     * 建议取值 0.82f ~ 0.90f。</p>
     */
    private Float outputQuality = 0.85f;

    /**
     * 水印图片绝对路径。
     */
    private String watermarkImagePath;

    /**
     * 水印位置。
     */
    private ImagePosition watermarkPosition = ImagePosition.BOTTOM_RIGHT;

    /**
     * 水印透明度。
     *
     * <p>范围建议 0.0f ~ 1.0f。</p>
     */
    private float watermarkOpacity = 0.35f;

    /**
     * 是否对原图生成水印图。
     *
     * <p>生成命名规则示例：xxx_0.jpg</p>
     */
    private boolean watermarkToOriginal = true;

    /**
     * 是否对压缩图生成带水印版本。
     *
     * <p>生成命名规则示例：xxx_2.jpg</p>
     */
    private boolean watermarkToCompressed = true;

    /**
     * 是否删除原图。
     *
     * <p>当生成了水印图或压缩图后可选择删除原图。</p>
     */
    private boolean deleteOriginalImage;

    public Integer getCriticalSizeKb() {
        return criticalSizeKb;
    }

    public ImageOptions setCriticalSizeKb(Integer criticalSizeKb) {
        this.criticalSizeKb = criticalSizeKb;
        return this;
    }

    public Integer getMaxWidth() {
        return maxWidth;
    }

    public ImageOptions setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public Integer getMaxHeight() {
        return maxHeight;
    }

    public ImageOptions setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public Double getScale() {
        return scale;
    }

    public ImageOptions setScale(Double scale) {
        this.scale = scale;
        return this;
    }

    public Float getOutputQuality() {
        return outputQuality;
    }

    public ImageOptions setOutputQuality(Float outputQuality) {
        this.outputQuality = outputQuality;
        return this;
    }

    public String getWatermarkImagePath() {
        return watermarkImagePath;
    }

    public ImageOptions setWatermarkImagePath(String watermarkImagePath) {
        this.watermarkImagePath = watermarkImagePath;
        return this;
    }

    public ImagePosition getWatermarkPosition() {
        return watermarkPosition;
    }

    public ImageOptions setWatermarkPosition(ImagePosition watermarkPosition) {
        this.watermarkPosition = watermarkPosition;
        return this;
    }

    public float getWatermarkOpacity() {
        return watermarkOpacity;
    }

    public ImageOptions setWatermarkOpacity(float watermarkOpacity) {
        this.watermarkOpacity = watermarkOpacity;
        return this;
    }

    public boolean isWatermarkToOriginal() {
        return watermarkToOriginal;
    }

    public ImageOptions setWatermarkToOriginal(boolean watermarkToOriginal) {
        this.watermarkToOriginal = watermarkToOriginal;
        return this;
    }

    public boolean isWatermarkToCompressed() {
        return watermarkToCompressed;
    }

    public ImageOptions setWatermarkToCompressed(boolean watermarkToCompressed) {
        this.watermarkToCompressed = watermarkToCompressed;
        return this;
    }

    public boolean isDeleteOriginalImage() {
        return deleteOriginalImage;
    }

    public ImageOptions setDeleteOriginalImage(boolean deleteOriginalImage) {
        this.deleteOriginalImage = deleteOriginalImage;
        return this;
    }
}