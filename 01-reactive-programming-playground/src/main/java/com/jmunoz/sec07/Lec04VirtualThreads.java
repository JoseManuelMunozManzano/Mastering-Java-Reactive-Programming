package com.jmunoz.sec07;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

// Project Reactor soporta Virtual Threads.
// Para ello, es necesario configurar la propiedad del sistema:
// System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "true");
//
// Quizás en el futuro, esta propiedad sea el comportamiento por defecto.
//
// ¿Cómo sabemos que se está ejecutando en un Virtual Thread? Usando el méto-do:
// Thread.currentThread().isVirtual()
public class Lec04VirtualThreads {

    private static final Logger log = LoggerFactory.getLogger(Lec04VirtualThreads.class);

    public static void main(String[] args) {

        // Para saber si ya no hace falta esta propiedad, comentar y ejecutar el código.
        System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "true");

        var flux = Flux.create(sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating {}", i);
                        sink.next(i);
                    }

                    sink.complete();
                })
                .doOnNext(v -> log.info("value: {}", v))
                .doFirst(() -> log.info("first1-{}", Thread.currentThread().isVirtual()))           // Se ejecuta en el nuevo thread y se pregunta si es Virtual Thread
                .subscribeOn(Schedulers.boundedElastic())                                           // Cambiamos a otro thread pool
                .doFirst(() -> log.info("first2"));                                                 // Se ejecuta en el main thread


        Runnable runnable = () -> flux.subscribe(Util.subscriber());
        Thread.ofPlatform().start(runnable);


        // Como ahora se va a ejecutar en un hilo diferente, debemos esperar un poco.
        Util.sleepSeconds(2);
    }
}
