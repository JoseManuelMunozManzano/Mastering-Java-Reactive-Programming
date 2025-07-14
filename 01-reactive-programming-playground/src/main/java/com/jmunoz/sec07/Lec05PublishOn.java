package com.jmunoz.sec07;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

// publishOn for downstream
public class Lec05PublishOn {

    private static final Logger log = LoggerFactory.getLogger(Lec05PublishOn.class);

    public static void main(String[] args) {

        // Con publishOn, cuando vamos desde el subscriber al producer, to-do se ejecuta en el current thread,
        // saltándose el publishOn.
        // Cuando ya se ha generado la data y vamos hacia el subscriber, entonces cuando llegamos al publishOn,
        // se cambia el thread pool, y se ejecuta el resto de la cadena en el nuevo thread pool.
        //
        // Podemos tener varios publishOn en la misma cadena, y cada uno de ellos cambiará el thread pool.
        var flux = Flux.create(sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating {}", i);
                        sink.next(i);
                    }

                    sink.complete();
                })
//                .publishOn(Schedulers.parallel())
                .doOnNext(v -> log.info("value: {}", v))
                .doFirst(() -> log.info("first1"))
                .publishOn(Schedulers.boundedElastic())
                .doFirst(() -> log.info("first2"));


        Runnable runnable = () -> flux.subscribe(Util.subscriber());
        Thread.ofPlatform().start(runnable);

        // Como ahora se va a ejecutar en un hilo diferente, debemos esperar un poco.
        Util.sleepSeconds(2);
    }
}
