package com.provisio.execution;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

// Starts the full Spring context, just like the real app would.
@SpringBootTest
// Pull in the test database config. Without it, this test has no database.
@Import(TestcontainersConfiguration.class)
class ExecutionApiApplicationTests {

	// Empty on purpose: fails if the app can't start (missing bean, bad config, no database...).
	@Test
	void contextLoads() {
	}
}
