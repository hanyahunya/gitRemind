package com.hanyahunya.gitRemind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GitRemindApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitRemindApplication.class, args);
	}

}
