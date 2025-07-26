package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Lec04ConcatError {

    private static final Logger log = LoggerFactory.getLogger(Lec04ConcatError.class);

    public static void main(String[] args) {
        // demo1();
        demo2();

        // delayElements (en producer1()) usa un thread pool separado, por lo que
        // no bloquea el hilo principal. Lo bloqueamos nosotros para que
        // no termine el programa.
        Util.sleepSeconds(3);
    }

    private static void demo1() {
        // El producer3 devuelve una señal de error y vamos a ver si producer2 emite elementos.
        // Vemos que no se emiten elementos una vez se emite una señal de error.
        producer1()
                .concatWith(producer3())
                .concatWith(producer2())
                .subscribe(Util.subscriber());
    }

    private static void demo2() {
        // Usando concatDelayError, el producer2 se ejecuta aunque el producer3 emita una señal de error.
        // El error se retrasa hasta que se completen todos los producers, satisfaciendo la petición del subscriber.
        Flux.concatDelayError(producer1(), producer3(), producer2())
                .subscribe(Util.subscriber());
    }

    private static Flux<Integer> producer1() {
        return Flux.just(1, 2, 3)
                .doOnSubscribe(s -> log.info("Subscribed to producer1"))
                .delayElements(Duration.ofMillis(10));
    }

    private static Flux<Integer> producer2() {
        return Flux.just(51, 52, 53)
                .doOnSubscribe(s -> log.info("Subscribed to producer2"))
                .delayElements(Duration.ofMillis(10));
    }

    // Producer que devuelve una señal de error.
    private static Flux<Integer> producer3() {
        return Flux.error(new RuntimeException("ooops!!"));
    }
}
