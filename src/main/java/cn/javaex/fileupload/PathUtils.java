package cn.javaex.fileupload;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 路径工具类
 */
public class PathUtils {
	
	/**
	 * 路径转换
	 * @param str
	 * @return
	 */
	public static String slashify(String str) {
		return str.replace('\\', '/');
	}
	
	/**
	 * 判断是否是绝对路径
	 * @param path
	 * @return
	 */
	public static boolean isAbsolutePath(String path) {
		path = path.substring(0, 2);
		return path.startsWith("/") || path.endsWith(":");
	}
	
	/**
	 * 获取项目所在磁盘的文件夹路径
	 * @return
	 */
	public static String getProjectPath() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		// 获取地址内容，原路径（项目名）
		String realPath = request.getSession().getServletContext().getRealPath("/");
		// tomcat webapps下部署
		if (realPath.indexOf("apache-tomcat")>0) {
			String path = request.getContextPath();    // 项目名
			path = path.replace("/", File.separator) + File.separator;
			return realPath.replace(path, "");
		}
		
		return System.getProperty("user.dir");
	}
	
	/**
	 * 获取服务路径
	 * @return
	 */
	public static String getServerPath() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		String domain = request.getScheme() + "://" + request.getServerName();
		int port = request.getServerPort();
		
		return port==80 ? domain : domain + ":" + port;
	}

}
