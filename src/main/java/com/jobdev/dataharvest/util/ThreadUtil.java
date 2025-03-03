package com.jobdev.dataharvest.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ThreadUtil {

    /**
     * Pausa a thread atual por um determinado número de milissegundos
     * 
     * @param millis tempo em milissegundos para pausar
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrompida durante sleep de {} ms", millis);
        }
    }

    /**
     * Executa uma tarefa em uma thread separada
     * 
     * @param task a tarefa a ser executada
     * @return a thread criada
     */
    public static Thread runAsync(Runnable task) {
        Thread thread = new Thread(task);
        thread.start();
        return thread;
    }
}
