package com.jmunoz.sec05;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

// Hasta ahora, en subscribe se añadía Util.subscriber()
// Recordar que también se puede añadir métodos callback para los eventos onNext, onComplete y onError.
// Pero también, en vez de crear esos métodos, se pueden añadir directamente como operadores, separadamente, haciéndolo más legible.
public class Lec05Subscribe {
    private static final Logger log = LoggerFactory.getLogger(Lec05Subscribe.class);

    public static void main(String[] args) {
        Flux.range(1, 10)
                .doOnNext(i -> log.info("received: {}", i))
                .doOnComplete(() -> log.info("completed!"))
                .doOnError(err -> log.error("error", err))
                .subscribe();
    }
}
