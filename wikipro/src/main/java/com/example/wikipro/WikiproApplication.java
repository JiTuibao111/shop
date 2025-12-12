package com.example.wikipro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.wikipro.mapper")
public class  WikiproApplication {

	public static void main(String[] args) {
		SpringApplication.run(WikiproApplication.class, args);
	}

}
