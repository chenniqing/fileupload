# fileupload
Local upload file, supporting relative path and absolute path
```
// 上传图片设置
ImageSetting setting = new ImageSetting();
setting.setGenerateByDate(true);     // 是否按照日期自动创建3级目录
setting.setMaxSize(500);             // 单个文件的大小上限（单位KB），默认不限制
//setting.setFileTypeArr(new String[]{"doc", "docx"});    // 文件类型限制，例如 new String[]{"doc", "docx"}。上传文件时默认不限制，上传文件时默认图片类型
setting.setCriticalValue(200);       // 图片压缩临界值（单位KB），超过此临界值才会进行压缩处理（非必填）
setting.setMaxWidth(600);            // 压缩之后的图片最大宽度（非必填）
setting.setMaxHeight(400);           // 压缩之后的图片最大高度（非必填）
//setting.setPosition(Positions.CENTER);    // 水印位置（默认为右下角）
setting.setWatermarkImagePath("D:\\test\\shuiyin.png");    // 水印图片的绝对路径
//setting.setOpacity(0.35f);                // 水印透明度（默认为 0.35）
setting.setWatermarkObjArr(new String[]{"big", "small"});    // 水印作用对象（默认大图和小图都添加水印，只有当水印图片的宽高都小于作用对象图片宽高1/3的时候才会添加水印）
//setting.setDeleteOriginalImage(false);    // 是否删除原图，默认不删除

Map<String, Object> map = null;
try {
	map = UploadUtils.uploadImage(file, uploadPath, setting);
} catch (FileFormatException e) {
	// 不支持的文件格式
} catch (FileSizeException e) {
	// 文件大小超过限制
} catch (UploadException e) {
	// 上传出错
} catch (Exception e) {
	e.printStackTrace();
}

if (map!=null) {
	System.out.println(map);
}
```
![javaex](http://img.javaex.cn/FgwBRBNOTYwvMewFxvuBqZ6HfAqR)
