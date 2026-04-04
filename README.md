# javaex-fileupload

> 轻量、易用、面向实际项目的 Java 文件上传工具包。  
> 同时覆盖 **普通文件上传、图片上传、Base64 上传、图片压缩/水印、断点续传**，并兼容 **Spring Boot 2 / Spring Boot 3+** 的接入方式。

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-8%2B-blue" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-2.x%20%7C%203.x-brightgreen" />
  <img alt="License" src="https://img.shields.io/badge/license-Apache--2.0-orange" />
  <img alt="Status" src="https://img.shields.io/badge/status-active-success" />
</p>

---

## 为什么选择 javaex-fileupload

很多上传工具要么功能太重，要么依赖过多，要么只适合某一种框架版本。  
`javaex-fileupload` 想解决的是更实际的问题：

- **API 简单**：尽量保持“拿来就能用”的体验
- **功能完整**：文件上传、图片上传、Base64、压缩、水印、断点续传一套覆盖
- **低耦合**：核心能力不强依赖 Servlet API
- **兼容性更好**：适配 Spring Boot 2 与 Spring Boot 3+
- **易扩展**：存储层、图片处理层、断点续传能力都可以继续演进

这不是一个为了“炫技”而设计的上传库，  
而是一个真正面向业务项目、后台管理系统、内容平台、企业项目的上传工具包。

---

## 核心特性

### 1. 普通文件上传
适用于文档、压缩包、音视频、附件等常见业务场景。

支持能力：

- 文件类型限制
- 文件大小限制
- 自动按日期生成目录
- 支持相对路径与绝对路径
- 支持生成访问 URL

### 2. 图片上传
除了基本上传外，还支持图片专属处理能力。

支持能力：

- 图片格式校验
- 真实图片内容识别
- 自动缩放
- JPEG 质量压缩
- 原图加水印
- 压缩图加水印
- 可选删除原图

### 3. Base64 上传
适合前端裁剪头像、Canvas 导出图片、移动端图片上传等场景。

支持能力：

- 支持 data URI
- 支持纯 Base64 文本
- 自动推断文件名与内容类型

### 4. 断点续传
为大文件上传提供更稳的基础能力。

支持能力：

- 创建上传会话
- 查询当前已上传偏移量
- 从指定 offset 继续上传
- 完成上传并转正式文件
- 中止上传

### 5. 兼容 Spring Boot 2 / 3+
这是 2.0 重点优化方向之一。

支持能力：

- 主包不强依赖 `javax.servlet-api`
- 主包不强依赖 `jakarta.servlet-api`
- 通过反射兼容 `Part` 与 `HttpServletResponse`
- 更适合做成通用工具包给不同项目复用

---

## 安装

### Maven

```xml
<dependency>
    <groupId>cn.javaex</groupId>
    <artifactId>fileupload</artifactId>
    <version>2.0.0</version>
</dependency>
```

> 版本号请替换为你实际发布的版本。

---

## 快速开始

### 1）上传普通文件

```java
UploadOptions options = new UploadOptions()
        .setUploadDir("static/upload/file")
        .setGenerateByDate(true)
        .setMaxSizeKb(10 * 1024)
        .setAllowedExtensions(new String[]{"pdf", "doc", "docx", "zip"})
        .setServerUrlPrefix("http://127.0.0.1:7001");

UploadResult result = UploadUtils2.uploadFile(file, options);
```

### 2）上传图片

```java
UploadOptions options = new UploadOptions()
        .setUploadDir("static/upload/image")
        .setGenerateByDate(true)
        .setMaxSizeKb(5 * 1024)
        .setAllowedExtensions(new String[]{"jpg", "jpeg", "png", "gif"})
        .setServerUrlPrefix("http://127.0.0.1:7001")
        .setImageOptions(
                new ImageOptions()
                        .setCriticalSizeKb(300)
                        .setMaxWidth(1280)
                        .setMaxHeight(1280)
                        .setOutputQuality(0.85f)
                        .setWatermarkImagePath("D:/watermark/logo.png")
                        .setWatermarkPosition(ImagePosition.BOTTOM_RIGHT)
                        .setWatermarkOpacity(0.35f)
                        .setWatermarkToOriginal(true)
                        .setWatermarkToCompressed(true)
                        .setDeleteOriginalImage(false)
        );

UploadResult result = UploadUtils2.uploadImage(file, options);
```

### 3）Base64 上传图片

```java
UploadOptions options = new UploadOptions()
        .setUploadDir("static/upload/avatar")
        .setGenerateByDate(true)
        .setServerUrlPrefix("http://127.0.0.1:7001");

UploadResult result = UploadUtils2.uploadBase64Image(base64, options);
```

---

## 返回结果示例

```java
result.getOldFileName();
result.getNewFileName();
result.getFileSizeKb();
result.getFileSizeStr();
result.getFileSuffix();
result.getFilePath();
result.getAbsolutePath();
result.getAccessUrl();
result.getWatermarkFilePath();
result.getSmallFilePath();
```

典型返回值含义：

- `filePath`：逻辑路径
- `absolutePath`：服务器磁盘绝对路径
- `accessUrl`：完整访问地址
- `watermarkFilePath`：原图水印版路径
- `smallFilePath`：压缩图路径

---

## 图片处理说明

`JdkImageProcessor` 默认使用 JDK 标准能力完成图片处理，不依赖 Thumbnailator。  
这意味着：

- 对 JPEG 压缩有较好的可控性
- 对缩放、水印等常见功能完全可用
- 适合希望减少第三方依赖的项目

建议：

- 照片类图片优先使用 JPEG
- 透明背景图优先保留 PNG
- 水印图请使用尺寸较小的 PNG
- `watermarkImagePath` 建议传绝对路径，且确保文件真实存在

---

## 断点续传示例

### 创建上传会话

```java
SimpleResumableUploadService resumableService = new SimpleResumableUploadService(
        new InMemoryUploadSessionRepository(),
        new FileSystemChunkStore("D:/upload-temp")
);

UploadSession session = resumableService.create(
        new CreateUploadSessionRequest()
                .setFileName("big-video.mp4")
                .setUploadDir("static/upload/video")
                .setTotalBytes(totalBytes)
);
```

### 追加分片

```java
UploadSession latest = resumableService.append(
        session.getUploadId(),
        offset,
        inputStream,
        chunkLength
);
```

### 完成上传

```java
UploadSession completed = resumableService.complete(session.getUploadId());
```

---

## 兼容性说明

### Spring Boot 2
可以正常接入。

### Spring Boot 3+
可以正常接入。

### 为什么可以同时兼容？
因为 2.0 的设计重点之一就是：

- 核心上传能力不直接绑定某个 Servlet 包名
- `Part` 使用反射兼容
- `HttpServletResponse` 使用反射兼容

这样做的好处是：

- 更适合做成公共工具包
- 不需要为不同项目强行绑定同一个 Servlet API
- 对业务方更友好

---

## 适合哪些项目

如果你的项目里存在以下需求，这个工具包会很合适：

- 后台管理系统附件上传
- 商品图片上传与压缩
- 用户头像上传与裁剪
- 富文本图片上传
- 文档中心 / 文件中心
- 大文件分片上传
- 企业内部平台统一文件上传组件

---

## 设计理念

`javaex-fileupload` 更关注三个词：

- **简单**：尽量少配置、少心智负担
- **实用**：优先解决真实业务问题
- **可维护**：核心层、存储层、图片层、Web 适配层职责清晰

它不是为了取代所有上传方案，  
而是希望成为你在 Java Web 项目里最顺手、最稳定的一把“上传工具刀”。

---

## 后续规划

2.0 之后可以继续扩展的方向包括：

- 更多图片格式支持
- 更强的 PNG 优化能力
- 对象存储扩展（OSS / MinIO / S3）
- 更完整的断点续传协议支持
- 更完善的 Spring Boot Starter
- 更丰富的示例与文档

---

## 参与贡献

欢迎提交：

- Issue
- Pull Request
- 功能建议
- Bug 反馈
- 文档改进

如果这个项目对你有帮助，欢迎点一个 **Star** ⭐

---

## License

Apache License 2.0
