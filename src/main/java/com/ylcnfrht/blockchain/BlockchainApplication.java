package com.ylcnfrht.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.ylcnfrht.blockchain")
@OpenAPIDefinition(info = @Info(title = "Blockchain API", version = "1.0.0", description = "A comprehensive blockchain REST API built with Spring Boot and DDD principles", contact = @Contact(name = "Blockchain Team", email = "blockchain@demo.com", url = "https://blockchain.demo.com"), license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")), servers = {
        @Server(url = "http://localhost:8080", description = "Development Server"),
        @Server(url = "https://api.blockchain.demo.com", description = "Production Server")
})
public class BlockchainApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlockchainApplication.class, args);
    }
}