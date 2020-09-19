package com.wk.kill.server;

import com.wk.kill.model.entity.ItemKill;
import com.wk.kill.server.service.IItemService;
import com.wk.kill.server.service.Impl.ItemService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

// 表明启动入口
@ImportResource(value = {"classpath:spring/spring-jdbc.xml"})
@MapperScan(value = "com.wk.kill.model.mapper")
@SpringBootApplication
@EnableScheduling // 开启定时任务
public class MainApplication extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MainApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }


}
