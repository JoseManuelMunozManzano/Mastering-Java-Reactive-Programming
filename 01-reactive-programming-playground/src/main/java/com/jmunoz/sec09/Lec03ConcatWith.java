package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Lec03ConcatWith {

    private static final Logger log = LoggerFactory.getLogger(Lec03ConcatWith.class);

    public static void main(String[] args) {
        // demo1();
        // demo2();
        demo3();

        // delayElements (en producer1()) usa un thread pool separado, por lo que
        // no bloquea el hilo principal. Lo bloqueamos nosotros para que
        // no termine el programa.
        Util.sleepSeconds(3);
    }

    private static void demo1() {
        // Usando concatWithValues(), primero se consumen los elementos de producer1() y luego los de producer2().
        // El resultado de este ejemplo es: 1, 2, 3, -1, 0
        // concatWithValues() no admite un iterable.
        producer1()
                .concatWithValues(-1, 0)
                .subscribe(Util.subscriber());
    }

    private static void demo2() {
        // Puede haber varios concatWith() y se ejecutan en orden de arriba a abajo.
        // El resultado de este ejemplo es: 1, 2, 3, 51, 52, 53
        // El resultado con take() es: 1, 2
        producer1()
                .concatWith(producer2())
                .take(2)
                .subscribe(Util.subscriber());
    }

    private static void demo3() {
        // Uso de un factory method.
        // Se concatenan estos publisher en el mismo orden que se declaran.
        // El resultado de este ejemplo es: 1, 2, 3, 51, 52, 53
        Flux.concat(producer1(), producer2())
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
}
