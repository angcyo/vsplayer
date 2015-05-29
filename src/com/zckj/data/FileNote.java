package com.zckj.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存当前路径下的所有文件名,与路径
 * <p/>
 * Created by angcyo on 2015-03-19 019.
 */
public class FileNote {
    public String currentFilePath;//当前路径
    public List<String> fileName;//文件名
    public List<String> filePath;//文件路径
    public List<String> fileFolderPath;//所有文件夹的路径
    public List<String> fileFolderName;//所有文件夹的名字
    public List<Boolean> hasChildFolder;//对应路径的文件夹是否有子文件夹

    public FileNote() {
        currentFilePath = "/";
        fileName = new ArrayList<String>();
        filePath = new ArrayList<String>();
        fileFolderName = new ArrayList<String>();
        fileFolderPath = new ArrayList<String>();
        hasChildFolder = new ArrayList<Boolean>();
    }
}
//修改于:2015年5月15日,星期五
//修改于:2015年5月15日,星期五
