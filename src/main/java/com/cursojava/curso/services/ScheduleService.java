package com.cursojava.curso.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuración del servicio de programación de tareas para la indexación de páginas web.
 */
@Configuration
@EnableScheduling
public class ScheduleService {

    @Autowired
    private SpiderService spiderService;

    /**
     * Programa la tarea de indexación de páginas web para que se ejecute a medianoche todos los días.
     * El método llama al método indexWebPages() del SpiderService.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleIndexWebPages() {
        spiderService.indexWebPages();
    }
}
