package cn.javaex.fileupload.image;

/**
 * 水印位置枚举。
 *
 * <p>用于替代 Thumbnailator 的 Position 类型，
 * 避免公开 API 依赖第三方图片库。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public enum ImagePosition {

    /**
     * 左上角。
     */
    TOP_LEFT,

    /**
     * 上中。
     */
    TOP_CENTER,

    /**
     * 右上角。
     */
    TOP_RIGHT,

    /**
     * 左中。
     */
    CENTER_LEFT,

    /**
     * 居中。
     */
    CENTER,

    /**
     * 右中。
     */
    CENTER_RIGHT,

    /**
     * 左下角。
     */
    BOTTOM_LEFT,

    /**
     * 下中。
     */
    BOTTOM_CENTER,

    /**
     * 右下角。
     */
    BOTTOM_RIGHT
}