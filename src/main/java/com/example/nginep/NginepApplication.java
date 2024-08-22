package com.example.nginep;

import com.example.nginep.config.RsaKeyConfigProperties;
import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@SpringBootApplication
@Log
public class NginepApplication {

	public static void main(String[] args) {
		SpringApplication.run(NginepApplication.class, args);
	}

}
