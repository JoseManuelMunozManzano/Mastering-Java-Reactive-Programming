package com.jmunoz.sec07;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

// Podemos tener varios operadores subscribeOn en un pipeline reactivo.
// El más cercano a la fuente de datos (al producer) es el que hace el trabajo.
// ¿Para qué sirve esto? Nos sirve, por ejemplo, si somos los dueños de una librería que establece
// un thread pool y no queremos que se cambie desde un proyecto que use nuestra librería.
//
// También se ve el uso de subscribeOn(Schedulers.immediate()), que sirve para no cambiar el thread pool actual.
// En este caso, el thread pool actual es boundedElastic, ya que no hemos especificado otro.
// ¿Para qué sirve esto? Para no cambiar el thread actual.
public class Lec03MultipleSubscribeOn {

    private static final Logger log = LoggerFactory.getLogger(Lec03MultipleSubscribeOn.class);

    public static void main(String[] args) {
        var flux = Flux.create(sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating {}", i);
                        sink.next(i);
                    }

                    sink.complete();
                })
                .subscribeOn(Schedulers.newParallel("jose"))    // Volvemos a cambiar de thread pool a parallel
//                .subscribeOn(Schedulers.immediate())                // No cambia el thread pool. Sigue con boundedElastic en este caso
                .doOnNext(v -> log.info("value: {}", v))
                .doFirst(() -> log.info("first1"))          // Se ejecuta en el nuevo thread
                .subscribeOn(Schedulers.boundedElastic())   // Cambiamos a otro thread pool
                .doFirst(() -> log.info("first2"));         // Se ejecuta en el main thread


        Runnable runnable = () -> flux.subscribe(Util.subscriber());
        Thread.ofPlatform().start(runnable);


        // Como ahora se va a ejecutar en un hilo diferente, debemos esperar un poco.
        Util.sleepSeconds(2);
    }
}
