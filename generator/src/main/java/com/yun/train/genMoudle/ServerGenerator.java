package com.yun.train.genMoudle;

import com.yun.train.util.DbUtil;
import com.yun.train.util.Field;
import com.yun.train.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerGenerator {

    static boolean readOnly = true;

//    static String vuePath = "web/src/views/main/";
    static String vuePath = "admin/src/views/main/";

    static String serverPath = "[module]/src/main/java/com/yun/train/";

    static String pomPath = "generator/pom.xml";
    static String module = "";
    static {
        new File(serverPath).mkdir();
    }

    public static void main(String[] args) throws Exception {
        String generatorPath = getGeneratorPath();

        module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");

        System.out.println("module:" + module);

        serverPath = serverPath.replace("[module]", module);
        System.out.println("servicePath:" + serverPath);

        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");
        System.out.println("connectionURL:" + connectionURL.getText() + "/" + userId.getText() + "/" + password.getText());

        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();

        String Domain = domainObjectName.getText();
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        String do_main = tableName.getText().replaceAll("_", "-");

        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> typeSet = getJavaTypes(fieldList);

        Map<String, Object> param = new HashMap<>();
        param.put("module", module);
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("tableNameCn", tableNameCn);
        param.put("typeSet", typeSet);
        param.put("fieldList", fieldList);
        param.put("readOnly", readOnly);
        System.out.println("组装参数：" + param);

        genModule(Domain, param, "service", "service");
//        会员端使用
//        genModule(Domain, param, "controller/web", "controller");
//        控台端使用
        genModule(Domain, param, "controller/admin", "adminController");

        genModule(Domain, param, "req", "saveReq");
        genModule(Domain, param, "req", "queryReq");
        genModule(Domain, param, "resp", "queryResp");

        genModuleVue(do_main, param);
    }

    private static void genModule(String Domain, Map<String, Object> param, String packageName, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = serverPath + packageName + "/";
        new File(toPath).mkdirs(); // Ensure the directory exists
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    private static void genModuleVue(String do_main, Map<String, Object> param) throws IOException, TemplateException {
        FreemarkerUtil.initConfig("vue.ftl");
        new File(vuePath+module).mkdirs(); // Ensure the directory exists
        String fileName = vuePath + module + "/" + do_main + ".vue";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    private static String getGeneratorPath() throws DocumentException {
        SAXReader reader = new SAXReader();
        Map<String, String> map = new HashMap<>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        reader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = reader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }

    private static Set<String> getJavaTypes(List<Field> fieldList) {
        Set<String> set = new HashSet<>();
        for (Field field : fieldList) {
            set.add(field.getJavaType());
        }
        return set;
    }
}
