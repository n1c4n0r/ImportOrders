package es.nmc.espublico.importorders.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3307/taskdb?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("secret");

        // Configuración para evitar thread starvation y clock leap
        hikariConfig.setMinimumIdle(5); // Establece un número mínimo de conexiones inactivas en el pool
        hikariConfig.setIdleTimeout(600000); // Tiempo máximo en milisegundos que una conexión puede estar inactiva antes de ser eliminada
        hikariConfig.setMaxLifetime(1800000); // Tiempo máximo en milisegundos que una conexión puede vivir en el pool
        hikariConfig.setConnectionTimeout(30000); // Tiempo máximo en milisegundos para esperar una conexión disponible

        return new HikariDataSource(hikariConfig);
    }
}

