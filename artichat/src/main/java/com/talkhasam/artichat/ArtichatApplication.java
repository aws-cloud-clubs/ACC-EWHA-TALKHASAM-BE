package com.talkhasam.artichat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
		exclude = { DataSourceAutoConfiguration.class }
)
public class ArtichatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtichatApplication.class, args);
	}

}
