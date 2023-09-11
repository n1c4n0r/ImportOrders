package es.nmc.espublico.importorders.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Número mínimo de hilos en el pool
        executor.setMaxPoolSize(60); // Número máximo de hilos en el pool
        //executor.setQueueCapacity(90000); // Capacidad máxima de la cola de tareas pendientes
        executor.setThreadNamePrefix("async-task-"); // Prefijo para los nombres de los hilos
        executor.initialize();
        return executor;
    }
}
