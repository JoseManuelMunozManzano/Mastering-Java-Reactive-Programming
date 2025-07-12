package com.jmunoz.sec06;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

// Usamos el méto-do share() para crear un hot publisher.
// Creamos un data producer para todos los subscribers.
/*
    Hot - 1 data producer para todos los subscribers.
    share => publish().refCount(1)
    Necesita como mínimo 1 subscriber para empezar a emitir elementos.
    Para cuando hay 0 subscribers.
    Re-subscription - Comienza desde el principio cuando hay un nuevo subscriber.
    Para que haya mínimo 2 subscribers, usamos publish().refCount(2).
*/
public class Lec02HotPublisher {

    private static final Logger log = LoggerFactory.getLogger(Lec02HotPublisher.class);

    public static void main(String[] args) {

        // Sin el méto-do share(), esto sería un cold publisher normal y corriente.
        // No comienza a emitir elementos hasta que haya un subscriber.
        //
         var movieFlux = movieStream().share();
        //
        // También vemos el méto-do publish().refCount(1) que es lo mismo que share().
        // En refCount indicamos el número mínimo de subscribers que necesitamos para empezar a emitir elementos (para cuando no hay subscribers).
        // El mínimo permitido es 1.
        //
        // var movieFlux = movieStream().publish().refCount(2);

        Util.sleepSeconds(2);

        // Sam ve la peli desde el principio, pero a los 4 minutos decide que ya ha visto suficiente y se va.
        // Si en vez de take(4) ponemos take(1), como todavía no ha empezado mike, para Mike empieza desde el principio!!!! (re-subscripción).
        movieFlux
                .take(4)
                // .take(1)
                .subscribe(Util.subscriber("sam"));

        Util.sleepSeconds(3);

        // Siendo un hot publisher, mike se pierde los 2 primeros elementos y empieza en el tercero.
        // A partir de ahí, tanto sam como mike ven todos los minutos de la película.
        // Pero mike decide a los dos minutos que no le gusta la película y se va.
        // take() manda un mensaje de cancelación al hot publisher, pero no funciona!!
        // No se puede cancelar.
        // Cuando no hay subscribers, el hot publisher por defecto se para.
        // Una vez parado, si se conecta un nuevo subscriber (re-subscripción), empieza desde el principio!!
        movieFlux
                .take(2)
                .subscribe(Util.subscriber("mike"));

        Util.sleepSeconds(15);
    }

    // cine
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
