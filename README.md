# fileupload
Local upload file, supporting relative path and absolute path
```
@PostMapping("upload")
@ResponseBody
public Result upload(MultipartFile file) {
	
	/**
	 * uploadPath可以是绝对路径 或 相对路径
	 *      绝对路径：D:\\test  或   /opt/upload
	 *      相对路径：upload    相对路径上传的文件会在项目同级目录生成的文件夹中
	 */
	String uploadPath = "upload";

	Map<String, Object> map = null;

	try {
		map = UploadUtils.uploadImage(file, uploadPath);
	} catch (FileFormatException e) {
		e.printStackTrace();    // 不支持的文件格式
	} catch (FileSizeException e) {
		e.printStackTrace();    // 文件大小超过限制
	} catch (UploadException e) {
		e.printStackTrace();    // 上传出错
	} catch (Exception e) {
		e.printStackTrace();
	}

	if (map!=null) {
		System.out.println(map);
	}
	
	return Result.success();
}
```
![javaex](https://images.gitee.com/uploads/images/2020/0329/201403_d7fbbbfb_1712536.png)


#### 安装

```
<dependency>
	<groupId>cn.javaex</groupId>
	<artifactId>fileupload</artifactId>
	<version>1.0.0</version>
</dependency>

<!-- 如果是基于spring的web项目，则下面2个依赖可以不用引入 -->
<dependency>
	<groupId>javax.servlet</groupId>
	<artifactId>javax.servlet-api</artifactId>
	<version>4.0.1</version>
	<scope>provided</scope>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-webmvc</artifactId>
	<version>4.3.7.RELEASE</version>
	<scope>provided</scope>
</dependency>
```


#### 论坛反馈地址
[http://bbs.javaex.cn/forumdisplay/7](http://bbs.javaex.cn/forumdisplay/7)


#### 插件文档地址

[http://doc.javaex.cn/document/37](http://doc.javaex.cn/document/37)


#### 官网
[http://www.javaex.cn](http://www.javaex.cn)
