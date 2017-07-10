package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
@EntityScan({"model"})
@ComponentScan({"services", "controllers", "persistence", "security", "model"})
@EnableJpaRepositories(basePackages={"repository"})
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}