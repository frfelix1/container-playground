package com.provisio.execution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * @SpringBootApplication is three annotations in one:
 *  - @Configuration:            this class may define beans.
 *  - @EnableAutoConfiguration:  look at the jars on the classpath and configure
 *                               things automatically (web starter present -> start Tomcat).
 *  - @ComponentScan:            find @Controller/@Service/... classes in THIS package
 *                               and its sub-packages. Classes outside com.provisio.execution
 *                               would silently be ignored.
 */
@SpringBootApplication
public class ExecutionApiApplication {

	public static void main(String[] args) {
		// Builds the Spring context (all beans), starts the embedded web server and blocks.
		SpringApplication.run(ExecutionApiApplication.class, args);
	}
}
