package com.yun.train.server;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerator {
    static String toPath="generator/src/main/java/com/yun/train/test/";
    static String pomPath="generator/pom.xml";
    static {
        new File(toPath).mkdir();
    }
    public static void main(String[] args) throws Exception{
//        读取xml
        SAXReader reader = new SAXReader();
        Map<String, String> map = new HashMap<>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        reader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = reader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());


//        FreemarkerUtil.initConfig("test.ftl");
//        Map<String, Object> data = new HashMap<>();
//        data.put("domain","Test");
//        FreemarkerUtil.generator(toPath+"Test.java", data);
    }
}
