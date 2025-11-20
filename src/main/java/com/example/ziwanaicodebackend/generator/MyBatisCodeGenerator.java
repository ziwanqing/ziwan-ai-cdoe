package com.example.ziwanaicodebackend.generator;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;

public class MyBatisCodeGenerator {

    private static final String PACKAGE_NAME = "com.example.ziwanaicodebackend.generator.generesult";

    // 数据库表名,要生成的表
    private static final String[] TABLE_NAMES = {"user"};

    public static void main(String[] args) {

//        Dict dict = YamlUtil.loadByPath("application.yml");
//        Map<String,Object> byPath = dict.getByPath("spring.datasource");
//        String url = String.valueOf(byPath.get("url"));
//        String username = String.valueOf(byPath.get("username"));
//        String password = String.valueOf(byPath.get("password"));


        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/ziwan_ai_code");
        dataSource.setUsername("root");
        dataSource.setPassword("QqAa123456");

        //创建配置内容
        GlobalConfig globalConfig = createGlobalConfigUseStyle2();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }



    public static GlobalConfig createGlobalConfigUseStyle2() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage(PACKAGE_NAME);

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setGenerateTable(TABLE_NAMES)
                //逻辑删除字段
                        .setLogicDeleteColumn("isDelete");

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(21);

        //设置生成 mapper
        globalConfig.enableMapper();
        globalConfig.enableMapperXml();
        globalConfig.enableService();
        globalConfig.enableServiceImpl();
        globalConfig.enableController();


        //生成注释，比如生成时间和作者
        globalConfig.getJavadocConfig()
                .setAuthor("<a href=\"https://github.com/ziwanqing\">紫菀</a>")
                .setSince("");



        return globalConfig;
    }
}