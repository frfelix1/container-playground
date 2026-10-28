package com.provisio.execution;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mssqlserver.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

/*
 * @TestConfiguration = extra beans for tests only. Lives in src/test, so it is
 * never part of the real app.
 * proxyBeanMethods = false: a small startup optimisation; we never call the
 * @Bean method ourselves, so Spring doesn't need to wrap this class.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	/*
	 * @Bean = Spring creates this object and manages its life: it starts the
	 * container before the tests and stops it after them.
	 *
	 * @ServiceConnection = Spring asks the container for its URL, user and
	 * password and uses them to connect. No hardcoded database settings.
	 */
	@Bean
	@ServiceConnection
	MSSQLServerContainer sqlServerContainer() {
		// Same image and tag you pulled. A different tag would make Testcontainers download another image.
		return new MSSQLServerContainer(DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-latest"))
				// SQL Server refuses to start until Microsoft's licence is accepted.
				// Code equivalent of ACCEPT_EULA=Y from the spike.
				.acceptLicense();
	}
}
