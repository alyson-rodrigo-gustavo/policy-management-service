package br.com.alysongustavo.policymanagementservice;

import org.springframework.boot.SpringApplication;

public class TestPolicyManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(PolicyManagementServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
