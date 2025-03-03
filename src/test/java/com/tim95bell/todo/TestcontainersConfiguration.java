package com.tim95bell.todo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
	String mariaDbImageName;

	TestcontainersConfiguration(@Value("#{environment.TIM95BELL_MARIADB_VERSION}") String mariaDbVersion) {
		this.mariaDbImageName = "mariadb:" + mariaDbVersion;
	}

	@Bean
	@ServiceConnection
	MariaDBContainer<?> mariaDbContainer() {
		return new MariaDBContainer<>(DockerImageName.parse(mariaDbImageName));
	}
}
