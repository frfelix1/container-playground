package com.provisio.execution;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Starts the full Spring context, just like the real app would.
@SpringBootTest
class ExecutionApiApplicationTests {

	// Empty on purpose: fails if the app can't start (missing bean, bad config, no database...).
	@Test
	void contextLoads() {
	}
}
