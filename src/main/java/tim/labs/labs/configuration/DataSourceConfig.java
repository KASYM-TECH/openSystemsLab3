package tim.labs.labs.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:54321/postgres");
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.setConnectionTimeout(60000);
        config.setMinimumIdle(10); // Set minimum idle connections
        config.setMaximumPoolSize(1000); // Set max pool size
        config.setDriverClassName("org.postgresql.Driver"); // Set the driver class


        return new HikariDataSource(config);
    }
}
