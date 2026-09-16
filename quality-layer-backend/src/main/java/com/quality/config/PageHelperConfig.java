package com.quality.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

@Configuration
public class PageHelperConfig {

    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties props = new Properties();
        // 数据库方言 mysql
        props.setProperty("dialect", "mysql");
        // 分页合理化：pageNum<=1自动查第一页，超过总页数查最后一页
        props.setProperty("reasonable", "true");
        // 支持mapper接口参数传分页
        props.setProperty("supportMethodsArguments", "true");
        // 返回count总数
        props.setProperty("returnPageInfo", "check");
        props.setProperty("params", "pageNum=pageNum;pageSize=pageSize");
        pageHelper.setProperties(props);
        return pageHelper;
    }
}