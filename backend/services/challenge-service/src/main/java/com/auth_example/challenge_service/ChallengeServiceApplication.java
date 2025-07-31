package com.auth_example.challenge_service;

import com.auth_example.common_service.config.CommonServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@Import(CommonServiceConfig.class)
public class ChallengeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeServiceApplication.class, args);
	}

}
