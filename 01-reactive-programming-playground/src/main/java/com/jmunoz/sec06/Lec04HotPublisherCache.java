package com.jmunoz.sec06;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

/*
    - publish().autoConnect(0) provee nuevos valores al subscriber.
    - replay nos permite cache
*/
public class Lec04HotPublisherCache {

    private static final Logger log = LoggerFactory.getLogger(Lec04HotPublisherCache.class);

    public static void main(String[] args) {

        // Usaremos replay() en vez de publish() para cachear los valores emitidos por el publisher.
        //
        // publish() tiene el problema que hace perder el primer elemento que corresponde a un subscriber.
        //
        // var stockFlux = stockStrem().publish().autoConnect(0);
        //
        // Esto se corrige usando replay() en vez de publish().
        // Por defecto, replay() intentará cachear to-do. Podemos indicarle los elementos a cachear (en el ejemplo 1).
        var stockFlux = stockStrem().replay(1).autoConnect(0);

        // Descomentar publish() para ver el problema.
        // Sam se une tras 4 sg, pero ese elemento no lo recibe. Recibe el siguiente.
        // Ese es el problema de publish.autoConnect(0), no cachea el primer elemento que le tocaría a Sam.
        // Para corregir esto, Reactor provee técnicas de cacheo.
        Util.sleepSeconds(4);

        log.info("sam joining");
        stockFlux.subscribe(Util.subscriber("sam"));

        // Descomentar publish() para ver el problema.
        // Mike se une tras 8 sg, pero ese elemento no lo recibe. Recibe el siguiente al que le tocaría.
        Util.sleepSeconds(4);

        log.info("mike joining");
        stockFlux.subscribe(Util.subscriber("mike"));

        Util.sleepSeconds(15);
    }

    private static Flux<Integer> stockStrem() {
        return Flux.generate(synchronousSink -> synchronousSink.next(Util.faker().random().nextInt(10, 100)))
                .delayElements(Duration.ofSeconds(3))   // Solo para la prueba.
                .doOnNext(price -> log.info("emitting price: {}", price))
                .cast(Integer.class);

    }
}
