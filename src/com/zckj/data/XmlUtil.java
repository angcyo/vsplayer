package com.zckj.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Created by angcyo on 2015-03-30 030.
 */
public class XmlUtil {

    /**
     * xml 文件路径 ,默认是SD卡根路径下的 XmlUtil.xml, 不支持多级路径
     */
    public static String xmlFilePath = FileUtil.getSDPath() + File.separator + "XmlUtil.xml";
    /**
     * XML 文件的根元素,默认set,生效需要调用 createNewXmlFile
     */
    public static String xmlRootElemnt = "set";

    /**
     * 用于修改key 对应的 valu 值,,如果key不存在,自动创建,并设置值
     */
    public static void putValue(String key, String value) throws DocumentException {
        if (key == null || value == null)
            return;

        List<Element> elements = getRootElementList();
        boolean isFind = false;
        for (Element element : elements) {//枚举所有元素
            if (element.getName().equalsIgnoreCase(key)) {
                element.setText(value);//修改值
                isFind = true;
            }
        }
        if (!isFind) {//如果没有找到需要修改的值,则进行添加操作
            addValue(key, value);
        }

        save(document);
    }

    /**
     * 获取key对应的 value, 如果没有找到返回null
     */
    public static String getValue(String key) throws DocumentException {
        if (key == null)
            return "";

        List<Element> elements = getRootElementList();

        for (Element element : elements) {//枚举所有元素
            if (element.getName().equalsIgnoreCase(key)) {
                return element.getText();
            }
        }
        return "";
    }


    /**
     * 移除key,删除的意思
     */
    public static void removeValue(String key) throws DocumentException {
        if (key == null)
            return;

        Document document = getDocument();
        List<Element> elements = document.getRootElement().elements();
        Element rootElement = document.getRootElement();

        for (Element element : elements) {//枚举所有元素
            if (element.getName().equalsIgnoreCase(key)) {
                rootElement.remove(element);//移除元素
            }
        }

        save(document);
    }

    public static void addValue(String key, String value) throws DocumentException {
        if (key == null || value == null)
            return;

        List<Element> elements = getRootElementList();
        boolean isFind = false;
        for (Element element : elements) {//枚举所有元素
            if (element.getName().equalsIgnoreCase(key)) {//添加的时候,是否允许添加重复的值?
                //在这里添加逻辑...
                isFind = true;
            }
        }
        if (!isFind) {//如果没有找到需要修改的值,则进行添加操作

        }

        getRootElement().addElement(key).setText(value);//最主要还是这一句,上面都可以不要

        save(document);
    }


    //以下代码可以不关心



    /**
     * 实例化xml文件,如果不存在自动创建带有 rootElement元素的 xml文件并保存在默认路径
     */
    private static void instantiation() {
        if (!isXmlExist()) {
            createNewXmlFile();
        }
    }

    private static Document getDocument() throws DocumentException {
        if (document == null) {
            instantiation();
            document = xmlReader.read(new File(xmlFilePath));
        }
        return document;
    }

    /**
     * 获取xml的根元素
     */
    private static Element getRootElement() throws DocumentException {
        instantiation();
        return getDocument().getRootElement();
    }

    /**
     * 获取xml根元素的所有元素
     */
    private static List<Element> getRootElementList() throws DocumentException {
        List<Element> elements = getRootElement().elements();
        return elements;
    }

    /**
     * 检测文件是否存在,用于创建新的xml文件
     */
    private static boolean isXmlExist() {
        return new File((xmlFilePath)).exists();
    }

    private static boolean haveRootElement() {
        return true;
    }

    public static Document createNewXmlFile() {
        return createNewXmlFile(null);
    }

    /**
     * 如果要创建一个指定的xml文件,请使用这个函数,并指定参数为文件的绝对路径
     */
    public static Document createNewXmlFile(String newFilePath) {
        Document document = DocumentHelper.createDocument();
        document.addElement(xmlRootElemnt);
        if (newFilePath == null || newFilePath.length() == 0) {
            save(document);
        } else {
            save(document, newFilePath);
        }
        return document;
    }

    private static void save(Document document) {
        save(document, null);
    }

    /**
     * 保存Document文档,一定要记得调用,否则修改无效
     */
    private static void save(Document document, String newFilePath) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        File file;
        if (newFilePath == null || newFilePath.length() == 0) {
            file = new File(xmlFilePath);
        } else {
            file = new File(newFilePath);
        }
        XMLWriter writer;
        try {
            writer = new XMLWriter(new FileOutputStream(file), format);
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SAXReader xmlReader = new SAXReader();
    private static Document document = null;//
}
//修改于:2015年5月15日,星期五
