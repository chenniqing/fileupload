package cn.javaex.fileupload.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import cn.javaex.fileupload.util.FileTypeUtils;

/**
 * 基于 JDK 标准库的图片处理器。
 *
 * <p>功能包括：
 * <br>1. 多步缩放
 * <br>2. JPEG 质量压缩
 * <br>3. 原图水印
 * <br>4. 压缩图水印</p>
 *
 * <p>说明：
 * <br>1. 本实现不依赖 Thumbnailator；
 * <br>2. JPEG 压缩效果可以达到较高质量；
 * <br>3. PNG 默认使用 JDK 重编码，若后续需要更极致压缩可再扩展高级实现。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class JdkImageProcessor implements ImageProcessor {

    /**
     * 原图水印后缀。
     */
    private static final String SUFFIX_WATERMARK = "_0";

    /**
     * 压缩图后缀。
     */
    private static final String SUFFIX_COMPRESS = "_1";

    /**
     * 压缩并带水印图后缀。
     */
    private static final String SUFFIX_COMPRESS_WATERMARK = "_2";

    /**
     * 处理图片。
     *
     * @param originalFile 原图文件
     * @param options      图片处理选项
     * @return 图片处理结果
     * @throws IOException 图片读写失败时抛出
     */
    @Override
    public ImageProcessResult process(File originalFile, ImageOptions options) throws IOException {
        ImageProcessResult result = new ImageProcessResult();
        result.setOriginalFile(originalFile);

        if (options == null) {
            return result;
        }

        BufferedImage source = ImageIO.read(originalFile);
        if (source == null) {
            return result;
        }

        String fileName = originalFile.getName();
        String ext = FileTypeUtils.getExtension(fileName);
        String baseName = FileTypeUtils.getBaseName(fileName);
        File parent = originalFile.getParentFile();

        BufferedImage watermarkImage = null;
        boolean hasWatermark = options.getWatermarkImagePath() != null && !options.getWatermarkImagePath().trim().isEmpty();
        if (hasWatermark) {
            watermarkImage = ImageIO.read(new File(options.getWatermarkImagePath()));
            if (watermarkImage == null) {
                hasWatermark = false;
            }
        }

        // 生成原图水印版本
        if (hasWatermark && options.isWatermarkToOriginal() && canApplyWatermark(source, watermarkImage)) {
            File watermarkFile = new File(parent, baseName + SUFFIX_WATERMARK + "." + ext);
            BufferedImage watermarked = applyWatermark(copyImage(source), watermarkImage,
                    options.getWatermarkPosition(), options.getWatermarkOpacity());
            writeImage(watermarked, ext, watermarkFile, options.getOutputQuality());
            result.setWatermarkFile(watermarkFile);
        }

        // 生成压缩图或压缩水印图
        if (shouldCompress(originalFile, options)) {
            BufferedImage target = resize(source, options);
            String targetSuffix = SUFFIX_COMPRESS;

            if (hasWatermark && options.isWatermarkToCompressed() && canApplyWatermark(target, watermarkImage)) {
                target = applyWatermark(target, watermarkImage,
                        options.getWatermarkPosition(), options.getWatermarkOpacity());
                targetSuffix = SUFFIX_COMPRESS_WATERMARK;
            }

            File smallFile = new File(parent, baseName + targetSuffix + "." + ext);
            writeImage(target, ext, smallFile, options.getOutputQuality());
            result.setSmallFile(smallFile);
        }

        // 按需删除原图
        if (options.isDeleteOriginalImage() && (result.getWatermarkFile() != null || result.getSmallFile() != null)) {
            result.setOriginalDeleted(originalFile.delete());
        }

        return result;
    }

    /**
     * 判断是否应该进行压缩或缩放。
     *
     * @param originalFile 原图文件
     * @param options      图片处理选项
     * @return 是否需要压缩
     */
    private boolean shouldCompress(File originalFile, ImageOptions options) {
        Integer criticalSizeKb = options.getCriticalSizeKb();
        if (criticalSizeKb == null || criticalSizeKb <= 0) {
            return hasResizeTarget(options);
        }
        long sizeKb = (originalFile.length() + 1023) / 1024;
        return sizeKb > criticalSizeKb && hasResizeTarget(options);
    }

    /**
     * 判断是否配置了有效的缩放目标。
     *
     * @param options 图片处理选项
     * @return 是否配置了缩放目标
     */
    private boolean hasResizeTarget(ImageOptions options) {
        return (options.getScale() != null && options.getScale() > 0 && options.getScale() <= 1.0D)
                || (options.getMaxWidth() != null && options.getMaxWidth() > 0)
                || (options.getMaxHeight() != null && options.getMaxHeight() > 0);
    }

    /**
     * 对图片执行缩放。
     *
     * @param source  原图
     * @param options 图片处理选项
     * @return 缩放后的图片
     */
    private BufferedImage resize(BufferedImage source, ImageOptions options) {
        double scale = calculateScale(source, options);
        if (scale >= 1.0D) {
            return copyImage(source);
        }

        int targetWidth = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int targetHeight = Math.max(1, (int) Math.round(source.getHeight() * scale));
        return multiStepResize(source, targetWidth, targetHeight);
    }

    /**
     * 计算最终缩放比例。
     *
     * @param source  原图
     * @param options 图片处理选项
     * @return 缩放比例
     */
    private double calculateScale(BufferedImage source, ImageOptions options) {
        if (options.getScale() != null && options.getScale() > 0 && options.getScale() <= 1.0D) {
            return options.getScale();
        }

        Integer maxWidth = options.getMaxWidth();
        Integer maxHeight = options.getMaxHeight();

        if ((maxWidth == null || maxWidth <= 0) && (maxHeight == null || maxHeight <= 0)) {
            return 1.0D;
        }

        double widthRatio = maxWidth == null || maxWidth <= 0 ? 1.0D : (double) maxWidth / source.getWidth();
        double heightRatio = maxHeight == null || maxHeight <= 0 ? 1.0D : (double) maxHeight / source.getHeight();

        double ratio = Math.min(widthRatio, heightRatio);
        return Math.min(ratio, 1.0D);
    }

    /**
     * 多步缩放。
     *
     * <p>相比一步到位缩放，多步缩放在较大尺寸压小时通常效果更好。</p>
     *
     * @param source       原图
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @return 缩放后的图片
     */
    private BufferedImage multiStepResize(BufferedImage source, int targetWidth, int targetHeight) {
        int currentWidth = source.getWidth();
        int currentHeight = source.getHeight();
        BufferedImage current = source;

        while (currentWidth / 2 >= targetWidth && currentHeight / 2 >= targetHeight) {
            currentWidth = currentWidth / 2;
            currentHeight = currentHeight / 2;
            current = scaleOnce(current, currentWidth, currentHeight);
        }

        if (currentWidth != targetWidth || currentHeight != targetHeight) {
            current = scaleOnce(current, targetWidth, targetHeight);
        }

        return current;
    }

    /**
     * 单步缩放。
     *
     * @param source       原图
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @return 缩放后的图片
     */
    private BufferedImage scaleOnce(BufferedImage source, int targetWidth, int targetHeight) {
        int imageType = source.getTransparency() == Transparency.OPAQUE
                ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;

        BufferedImage target = new BufferedImage(targetWidth, targetHeight, imageType);
        Graphics2D g = target.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    /**
     * 给图片添加水印。
     *
     * @param target    目标图
     * @param watermark 水印图
     * @param position  水印位置
     * @param opacity   水印透明度
     * @return 添加水印后的图片
     */
    private BufferedImage applyWatermark(BufferedImage target, BufferedImage watermark,
                                         ImagePosition position, float opacity) {
        BufferedImage result = copyImage(target);
        Graphics2D g = result.createGraphics();
        try {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, normalizeOpacity(opacity)));
            int[] xy = calculateWatermarkPosition(result, watermark, position);
            g.drawImage(watermark, xy[0], xy[1], null);
        } finally {
            g.dispose();
        }
        return result;
    }

    /**
     * 规范化透明度。
     *
     * @param opacity 原始透明度
     * @return 0.0f ~ 1.0f 范围内的透明度
     */
    private float normalizeOpacity(float opacity) {
        if (opacity < 0f) {
            return 0f;
        }
        if (opacity > 1f) {
            return 1f;
        }
        return opacity;
    }

    /**
     * 计算水印绘制位置。
     *
     * @param target    目标图
     * @param watermark 水印图
     * @param position  位置
     * @return 长度为 2 的数组：[x, y]
     */
    private int[] calculateWatermarkPosition(BufferedImage target, BufferedImage watermark, ImagePosition position) {
        int margin = 10;
        int x;
        int y;

        switch (position) {
            case TOP_LEFT:
                x = margin;
                y = margin;
                break;
            case TOP_CENTER:
                x = (target.getWidth() - watermark.getWidth()) / 2;
                y = margin;
                break;
            case TOP_RIGHT:
                x = target.getWidth() - watermark.getWidth() - margin;
                y = margin;
                break;
            case CENTER_LEFT:
                x = margin;
                y = (target.getHeight() - watermark.getHeight()) / 2;
                break;
            case CENTER:
                x = (target.getWidth() - watermark.getWidth()) / 2;
                y = (target.getHeight() - watermark.getHeight()) / 2;
                break;
            case CENTER_RIGHT:
                x = target.getWidth() - watermark.getWidth() - margin;
                y = (target.getHeight() - watermark.getHeight()) / 2;
                break;
            case BOTTOM_LEFT:
                x = margin;
                y = target.getHeight() - watermark.getHeight() - margin;
                break;
            case BOTTOM_CENTER:
                x = (target.getWidth() - watermark.getWidth()) / 2;
                y = target.getHeight() - watermark.getHeight() - margin;
                break;
            case BOTTOM_RIGHT:
            default:
                x = target.getWidth() - watermark.getWidth() - margin;
                y = target.getHeight() - watermark.getHeight() - margin;
                break;
        }

        return new int[] { Math.max(0, x), Math.max(0, y) };
    }

    /**
     * 判断是否适合添加水印。
     *
     * <p>默认要求水印宽高都小于目标图的 1/3。</p>
     *
     * @param target    目标图
     * @param watermark 水印图
     * @return 是否适合添加水印
     */
    private boolean canApplyWatermark(BufferedImage target, BufferedImage watermark) {
        return watermark.getWidth() < target.getWidth() / 3
                && watermark.getHeight() < target.getHeight() / 3;
    }

    /**
     * 复制图片对象。
     *
     * @param source 原图
     * @return 新的图片副本
     */
    private BufferedImage copyImage(BufferedImage source) {
        int imageType = source.getTransparency() == Transparency.OPAQUE
                ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), imageType);
        Graphics2D g = copy.createGraphics();
        try {
            g.drawImage(source, 0, 0, null);
        } finally {
            g.dispose();
        }
        return copy;
    }

    /**
     * 将图片写出到目标文件。
     *
     * @param image      图片对象
     * @param ext        扩展名
     * @param targetFile 目标文件
     * @param quality    输出质量
     * @throws IOException 写文件失败时抛出
     */
    private void writeImage(BufferedImage image, String ext, File targetFile, Float quality) throws IOException {
        String lowerExt = ext.toLowerCase(Locale.ROOT);

        if ("jpg".equals(lowerExt) || "jpeg".equals(lowerExt)) {
            writeJpeg(image, targetFile, quality == null ? 0.85f : quality);
            return;
        }

        if (!ImageIO.write(image, lowerExt, targetFile)) {
            throw new IOException("No writer found for format: " + lowerExt);
        }
    }

    /**
     * 写出 JPEG 图片。
     *
     * @param image      原图
     * @param targetFile 目标文件
     * @param quality    输出质量
     * @throws IOException 写文件失败时抛出
     */
    private void writeJpeg(BufferedImage image, File targetFile, float quality) throws IOException {
        BufferedImage rgbImage = toRgb(image);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IOException("No JPEG writer found");
        }

        ImageWriter writer = writers.next();
        try (FileOutputStream fos = new FileOutputStream(targetFile);
             ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {

            writer.setOutput(ios);

            JPEGImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(normalizeQuality(quality));
            param.setOptimizeHuffmanTables(true);
            param.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);

            writer.write(null, new IIOImage(rgbImage, null, null), param);
            ios.flush();
        } finally {
            writer.dispose();
        }
    }

    /**
     * 规范化 JPEG 输出质量。
     *
     * @param quality 原始质量值
     * @return 规范化后的质量值
     */
    private float normalizeQuality(float quality) {
        if (quality < 0.1f) {
            return 0.1f;
        }
        if (quality > 1.0f) {
            return 1.0f;
        }
        return quality;
    }

    /**
     * 将图片转换为 RGB。
     *
     * <p>JPEG 不支持透明通道，因此写出 JPEG 之前应转换为 RGB。</p>
     *
     * @param source 原图
     * @return RGB 图片
     */
    private BufferedImage toRgb(BufferedImage source) {
        if (source.getType() == BufferedImage.TYPE_INT_RGB) {
            return source;
        }
        BufferedImage rgb = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
        op.filter(source, rgb);
        return rgb;
    }
}