package sk.r3n.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sk.r3n.jdbc.PostgreSqlBuilder;
import sk.r3n.jdbc.SqlBuilder;

@SpringBootApplication
public class ExampleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

    @Bean
    public SqlBuilder sqlBuilder() {
        return new PostgreSqlBuilder();
    }
}
