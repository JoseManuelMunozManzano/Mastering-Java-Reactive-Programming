package com.jmunoz.sec06;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

/*
    autoConnect es casi lo mismo que publish().refCount(1), con las siguientes diferencias:
    - NO para cuando no hay subscribers. Así que comienza a emitir elementos desde el principio, con 0 subscribers.
    - Lo hace verdaderamente un hot publisher - publish().autoConnect(0)
*/
public class Lec03HotPublisherAutoConnect {

    private static final Logger log = LoggerFactory.getLogger(Lec03HotPublisherAutoConnect.class);

    public static void main(String[] args) {
        // Usamos el méto-do autoConnect(0) para crear un hot publisher (de verdad).
        // Comienza a emitir elementos desde el principio, incluso con 0 subscribers.
        // Si no se indica 0 (subscriber), por defecto es 1 (subscriber).
        var movieFlux = movieStream().publish().autoConnect(0);

        Util.sleepSeconds(2);

        // Sam ve la peli desde el minuto 3, pero a los 4 minutos decide que ya ha visto suficiente y se va (a los 6 minutos se va).
        movieFlux
                .take(4)
                .subscribe(Util.subscriber("sam"));

        Util.sleepSeconds(3);

        // Mike se pierde los 5 primeros minutos y empieza en el sexto, pero para 3 minutos después (se va a los 8 minutos).
        // Ya la peli no la ve nadie, pero sigue emitiéndose.
        movieFlux
                .take(3)
                .subscribe(Util.subscriber("mike"));

        Util.sleepSeconds(15);
    }

    private static Flux<String> movieStream() {
        return Flux.generate(
                        () -> {
                            // Para saber cuántas veces se invoca.
                            log.info("received the request");
                            return 1;
                        },
                        (state, sink) -> {
                            var scene = "movie scene " + state;
                            log.info("playing {}", scene);
                            sink.next(scene);
                            return ++state;
                        }
                )
                .take(10)
                .delayElements(Duration.ofSeconds(1))   // Solo para la prueba.
                .cast(String.class);

    }
}
