package com.wyj.shiropro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * @author wangyajing
 *
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.wyj.shiropro.mapper"})
public class ShiroproApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroproApplication.class, args);
    }

}
