package br.com.fiap.vagazero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class VagaZeroApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VagaZeroApiApplication.class, args);
    }
}
