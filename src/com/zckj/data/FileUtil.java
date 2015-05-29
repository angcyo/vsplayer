package com.zckj.data;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.linux.vshow.R;

/**
 * Created by angcyo on 2015-03-19 019.
 */
public class FileUtil {

	/**
	 * 返回SD卡路径,如果没有返回默认的下载缓存路径
	 *
	 * @return the sD path
	 */
	public static String getSDPath() {
		return isExternalStorageWritable() ? Environment
				.getExternalStorageDirectory().getPath() : Environment
				.getDownloadCacheDirectory().getPath();
	}

	/**
	 * 判断是否有SD卡
	 *
	 * @return the boolean
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static FileNote getFileNote(String filePath) {
		if (filePath == null)
			return null;

		FileNote fileNote = new FileNote();
		fileNote.currentFilePath = filePath;

		File f = new File(filePath);
		if (!f.exists())
			return null;
		
		if (filePath.equalsIgnoreCase("/mnt") || filePath.equalsIgnoreCase("/mnt/")) {
			fileNote.fileFolderName.add("本地存储");
			fileNote.fileFolderPath.add("/mnt/sdcard");
			
			if (Environment.MEDIA_MOUNTED.equals("/mmt/extsd")) {
				fileNote.fileFolderName.add("外部存储");
				fileNote.fileFolderPath.add("/mnt/extsd");
			}
			
			return fileNote;
		}

		File[] files = f.listFiles();

		if (files == null)
			return null;

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			fileNote.fileName.add(file.getName());
			fileNote.filePath.add(file.getPath());

			// 过滤文件夹
			// if (file.isDirectory() && !f.getName().startsWith(".")
			// && file.canRead() && file.canWrite()
			// && !file.isHidden())

			if (file.canRead() && file.isDirectory() && file.canWrite()) {
				fileNote.fileFolderName.add(file.getName());
				fileNote.fileFolderPath.add(file.getPath());
				//
				// List<File> listFolder = listFolder(file.getPath());
				// if (listFolder == null || listFolder.size() < 1) {
				// fileNote.hasChildFolder.add(Boolean.FALSE);
				// } else {
				//
				// Logger.e("路径" + file.getPath() + "  含有" +
				// listFolder.size());
				// fileNote.hasChildFolder.add(Boolean.TRUE);
				// }

			}
		}

		// 默认的排序方式
		Collections.sort(fileNote.fileName);
		Collections.sort(fileNote.filePath);
		Collections.sort(fileNote.fileFolderName);
		Collections.sort(fileNote.fileFolderPath);

		// Logger.e("当前路径:::" + fileNote.currentFilePath + ":::文件夹数量::::" +
		// fileNote.fileFolderPath.size());

		return fileNote;
	}

	/**
	 * 枚举指定路径下的所有文件夹
	 */
	public static List<File> listFolder(String path) {
		if (Util.isEmpty(path)) {
			return null;
		}

		File file = new File(path);
		if (!file.exists() || !file.isDirectory() || !file.canRead()
				|| file.isHidden()) {
			return null;
		}

		File[] files = file.listFiles();
		if (files == null || files.length < 1) {
			return null;
		}

		List<File> listFolder = new ArrayList<File>();
		for (File temp : files) {
			if (temp.isDirectory() && !file.isHidden() && temp.canWrite()) {
				listFolder.add(temp);
			}
		}

		return listFolder;
	}

	/**
	 * 获取文件扩展名
	 *
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return null;
		}
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	/**
	 * 获取文件名,不包括扩展名
	 *
	 * @param fileName
	 * @return
	 */
	public static String getFileName(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return null;
		}
		int index = fileName.lastIndexOf(".");
		if (index != -1 && index != 0) {
			return fileName.substring(0, index);
		} else {
			return "";
		}
	}

	/**
	 * 获取APP根目录
	 */
	public static String getAppRootPath(Context context) {
		return getCurrentPathPrev(context.getFilesDir().toString());
	}

	/**
	 * 获得上一级目录
	 */
	public static String getCurrentPathPrev(String path) {
		if (path == null || path.length() == 0 || path.equals("/"))
			return "/";
		// String[] paths = path.split("/", 1);//至少返回1个
		String prevPath = path.substring(0, path.lastIndexOf("/") == 0 ? 1
				: path.lastIndexOf("/"));// 上一级路径
		// String basePath = "/storage/emulated";
		// if (prevPath.equals(basePath))
		// return basePath.concat("/0");
		return prevPath;
	}

	/**
	 * 计算SD卡的剩余空间
	 *
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 获取目录文件大小
	 *
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 转换文件大小
	 *
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取文件大小
	 *
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 获取文件大小
	 *
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 删除文件,文件夹,包含子文件
	 */
	public static boolean deleteFiles(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
				return true;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFiles(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 复制文件
	 */
	public static boolean copyFile(File fileFrom, File fileTo) {
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(fileFrom);
			output = new FileOutputStream(fileTo);
			int in = input.read();
			while (in != -1) {
				output.write(in);
				in = input.read();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				input.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 复制文件
	public static void copyFile2(File sourceFile, File targetFile)
			throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		// 刷新此缓冲的输出流
		outBuff.flush();

		//关闭流
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	// 复制文件夹
	public static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new
						File(new File(targetDir).getAbsolutePath()
						+ File.separator + file[i].getName());
				copyFile2(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}
}


//修改于:2015年5月15日,星期五
