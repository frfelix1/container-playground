package com.provisio.provisioning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Same shape as ExecutionApiApplication; component scan covers com.provisio.provisioning and below.
@SpringBootApplication
public class ProvisioningApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProvisioningApiApplication.class, args);
    }
}
