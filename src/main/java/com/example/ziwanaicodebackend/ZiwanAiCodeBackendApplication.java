package com.example.ziwanaicodebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.ziwanaicodebackend.mapper")
public class ZiwanAiCodeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZiwanAiCodeBackendApplication.class, args);
    }

}
