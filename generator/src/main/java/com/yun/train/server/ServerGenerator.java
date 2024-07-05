package com.yun.train.server;

import com.yun.train.util.FreemarkerUtil;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerator {
    static String toPath="generator/src/main/java/com/yun/train/test/";
    static {
        new File(toPath).mkdir();
    }
    public static void main(String[] args) throws IOException, TemplateException {
        FreemarkerUtil.initConfig("test.ftl");
        Map<String, Object> data = new HashMap<>();
        data.put("domain","Test");
        FreemarkerUtil.generator(toPath+"Test.java", data);
    }
}
