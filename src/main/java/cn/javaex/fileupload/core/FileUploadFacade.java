package cn.javaex.fileupload.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import cn.javaex.fileupload.exception.FileFormatException;
import cn.javaex.fileupload.image.ImageOptions;
import cn.javaex.fileupload.image.ImageProcessResult;
import cn.javaex.fileupload.image.ImageProcessor;
import cn.javaex.fileupload.storage.StorageEngine;
import cn.javaex.fileupload.util.DigestUtils;
import cn.javaex.fileupload.util.FileTypeUtils;
import cn.javaex.fileupload.util.PathUtils;

/**
 * 文件上传门面类。
 *
 * <p>负责协调以下流程：
 * <br>1. 基础校验
 * <br>2. 临时落盘
 * <br>3. 文件摘要计算
 * <br>4. 最终命名
 * <br>5. 图片后处理
 * <br>6. 上传结果组装</p>
 *
 * <p>该类本身不依赖 Servlet、Spring 或 Thumbnailator，
 * 适合作为 2.0 的核心上传入口。</p>
 *
 * @author 陈霓清
 * @since 2.0.0
 */
public class FileUploadFacade {

    /**
     * 存储引擎。
     */
    private final StorageEngine storageEngine;

    /**
     * 上传校验器。
     */
    private final UploadValidator uploadValidator;

    /**
     * 图片处理器。
     *
     * <p>当上传普通文件时不会使用；
     * 当上传图片且配置了图片选项时才会生效。</p>
     */
    private final ImageProcessor imageProcessor;

    /**
     * 创建一个上传门面实例。
     *
     * @param storageEngine   存储引擎
     * @param uploadValidator 上传校验器
     * @param imageProcessor  图片处理器
     */
    public FileUploadFacade(StorageEngine storageEngine,
                            UploadValidator uploadValidator,
                            ImageProcessor imageProcessor) {
        this.storageEngine = storageEngine;
        this.uploadValidator = uploadValidator;
        this.imageProcessor = imageProcessor;
    }

    /**
     * 上传普通文件。
     *
     * @param source  上传源
     * @param options 上传选项
     * @return 上传结果
     * @throws IOException 文件读写失败时抛出
     */
    public UploadResult uploadFile(UploadSource source, UploadOptions options) throws IOException {
        return doUpload(source, options, false);
    }

    /**
     * 上传图片。
     *
     * @param source  上传源
     * @param options 上传选项
     * @return 上传结果
     * @throws IOException 文件读写失败时抛出
     */
    public UploadResult uploadImage(UploadSource source, UploadOptions options) throws IOException {
        return doUpload(source, options, true);
    }

    /**
     * 执行实际上传逻辑。
     *
     * @param source    上传源
     * @param options   上传选项
     * @param imageMode 是否按图片模式处理
     * @return 上传结果
     * @throws IOException 文件读写失败时抛出
     */
    private UploadResult doUpload(UploadSource source, UploadOptions options, boolean imageMode) throws IOException {
        uploadValidator.validateBasic(source, options);

        String originalFileName = source.getOriginalFilename();
        String ext = FileTypeUtils.getExtension(originalFileName);

        if (imageMode) {
            uploadValidator.validateImageFileName(originalFileName);
        }

        File physicalDir = PathUtils.resolvePhysicalDirectory(options.getUploadDir(), options.isGenerateByDate());
        String logicalDir = PathUtils.resolveLogicalDirectory(options.getUploadDir(), options.isGenerateByDate());

        // 先以随机临时名落盘，避免在落盘前就依赖摘要值
        String tempName = UUID.randomUUID().toString().replace("-", "") + ".tmp";
        StoredFile storedTemp = storageEngine.save(source, physicalDir, tempName);

        // 计算 MD5 作为最终命名
        String md5 = DigestUtils.md5Hex(storedTemp.getFile().toPath());
        String finalName = md5 + "." + ext;
        File finalFile = new File(physicalDir, finalName);

        // 若同名文件已存在，则直接复用；否则将临时文件转正
        if (finalFile.exists()) {
            storedTemp.getFile().delete();
        } else {
            Files.move(storedTemp.getFile().toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // 图片模式下，必须校验真实内容是否真的是图片
        if (imageMode && !FileTypeUtils.isImage(finalFile)) {
            finalFile.delete();
            throw new FileFormatException("invalid image content");
        }

        UploadResult result = buildBaseResult(originalFileName, ext, logicalDir, finalFile, options);

        ImageOptions imageOptions = options.getImageOptions();
        if (imageMode && imageOptions != null && imageProcessor != null) {
            ImageProcessResult imageResult = imageProcessor.process(finalFile, imageOptions);
            fillImageResult(result, logicalDir, options, imageResult);
        }

        return result;
    }

    /**
     * 构建基础上传结果。
     *
     * @param oldFileName 原始文件名
     * @param ext         扩展名
     * @param logicalDir  逻辑目录
     * @param finalFile   最终文件
     * @param options     上传选项
     * @return 上传结果
     */
    private UploadResult buildBaseResult(String oldFileName, String ext,
                                         String logicalDir, File finalFile, UploadOptions options) {
        UploadResult result = new UploadResult();
        String logicalFilePath = PathUtils.concat(logicalDir, finalFile.getName());

        result.setOldFileName(oldFileName);
        result.setNewFileName(finalFile.getName());
        result.setFileSuffix(ext);
        result.setFileSizeKb((finalFile.length() + 1023) / 1024);
        result.setFileSizeStr(formatSize(finalFile.length()));
        result.setFilePath(logicalFilePath);
        result.setAbsolutePath(finalFile.getAbsolutePath());
        result.setAccessUrl(PathUtils.buildAccessUrl(options.getServerUrlPrefix(), logicalFilePath));

        return result;
    }

    /**
     * 填充图片衍生文件结果。
     *
     * @param result      上传结果
     * @param logicalDir  逻辑目录
     * @param options     上传选项
     * @param imageResult 图片处理结果
     */
    private void fillImageResult(UploadResult result, String logicalDir,
                                 UploadOptions options, ImageProcessResult imageResult) {
        if (imageResult.getWatermarkFile() != null) {
            String path = PathUtils.concat(logicalDir, imageResult.getWatermarkFile().getName());
            result.setWatermarkFilePath(path);
            result.setWatermarkAbsolutePath(imageResult.getWatermarkFile().getAbsolutePath());
            result.setWatermarkAccessUrl(PathUtils.buildAccessUrl(options.getServerUrlPrefix(), path));
        }

        if (imageResult.getSmallFile() != null) {
            String path = PathUtils.concat(logicalDir, imageResult.getSmallFile().getName());
            result.setSmallFilePath(path);
            result.setSmallAbsolutePath(imageResult.getSmallFile().getAbsolutePath());
            result.setSmallAccessUrl(PathUtils.buildAccessUrl(options.getServerUrlPrefix(), path));
        }
    }

    /**
     * 将字节数格式化为易读字符串。
     *
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
    private String formatSize(long bytes) {
        long kb = (bytes + 1023) / 1024;
        if (kb < 1024) {
            return kb + " KB";
        }
        long mb = kb / 1024;
        if (mb < 1024) {
            return mb + " MB";
        }
        long gb = mb / 1024;
        return gb + " GB";
    }
}