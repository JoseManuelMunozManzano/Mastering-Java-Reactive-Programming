package com.jmunoz.sec07;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Lec02SubscribeOn {

    private static final Logger log = LoggerFactory.getLogger(Lec02SubscribeOn.class);

    public static void main(String[] args) {

        var flux = Flux.create(sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating {}", i);
                        sink.next(i);
                    }

                    sink.complete();
                })
                .doOnNext(v -> log.info("value: {}", v))
                .doFirst(() -> log.info("first1"))          // Se ejecuta en el nuevo thread, esto y to-do lo de arriba (for upstream)
                .subscribeOn(Schedulers.boundedElastic())   // Cambiamos a otro thread
                .doFirst(() -> log.info("first2"));         // Se ejecuta en el main thread


//        flux.subscribe(Util.subscriber());

        // Vamos a hacer un Runnable para ver que ocurre con dos subscribers.
        // Lo que vemos es lo mismo que ocurre arriba, pero ahora empieza a ejecutarse en un thread diferente en vez del main thread.
        // Pero luego cambia igual a otro thread.
        //
        Runnable runnable1 = () -> flux.subscribe(Util.subscriber("sub1"));
        Runnable runnable2 = () -> flux.subscribe(Util.subscriber("sub2"));
        Thread.ofPlatform().start(runnable1);
        Thread.ofPlatform().start(runnable2);


        // Como ahora se va a ejecutar en un hilo diferente, debemos esperar un poco.
        Util.sleepSeconds(2);
    }
}
