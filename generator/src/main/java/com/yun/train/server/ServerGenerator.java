package com.yun.train.server;

import com.yun.train.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerator {
    static String serverPath ="member/src/main/java/com/yun/train/";
    static String pomPath="generator/pom.xml";
    static {
        new File(serverPath).mkdir();
    }
    public static void main(String[] args) throws Exception{
//        读取xml获取mybatis-generator
        String generatorPath = getGeneratorPath();

//        比如generatoe-config-member.xml,得到module=member
        String module = generatorPath.replace("[module]/src/main/resources/generator-config-","").replace(".xml","");
        System.out.println("module:"+module);

        serverPath=serverPath.replace("[module]",module);
        System.out.println("servicePath:"+serverPath);


//        读xml里面的节点table

        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText()+"/"+domainObjectName.getText());

        // 示例：表名 Yunyop_test
        // Domain = YunyopTest
        String Domain = domainObjectName.getText();
        // domain = yunyopTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = yunyop-test
        String do_main = tableName.getText().replaceAll("_", "-");
//        组装参数
        Map<String,Object> param = new HashMap<>();
        param.put("Domain",Domain);
        param.put("domain",domain);
        param.put("do_main",do_main);
        System.out.println("组装参数："+param);
        // 表中文名
//        String tableNameCn = DbUtil.getTableComment(tableName.getText());
//        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
//        Set<String> typeSet = getJavaTypes(fieldList);

        genModule(Domain, param,"service");

        genModule(Domain, param,"controller");
    }

    private static void genModule(String Domain, Map<String, Object> param,String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target+".ftl");
        String toPath = serverPath+target+"/";
        new File(serverPath).mkdir();
        String Target = target.substring(0,1).toUpperCase()+target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成："+fileName);
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
}
