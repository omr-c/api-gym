package com.snzalx.gym.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {
    // Esta clase habilita las tareas programadas y la ejecución asíncrona en la aplicación.
}
