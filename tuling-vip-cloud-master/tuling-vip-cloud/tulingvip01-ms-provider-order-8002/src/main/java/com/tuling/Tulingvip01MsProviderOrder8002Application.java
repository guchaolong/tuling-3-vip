package com.tuling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Tulingvip01MsProviderOrder8002Application {

	public static void main(String[] args) {
		SpringApplication.run(Tulingvip01MsProviderOrder8002Application.class, args);
	}

}
