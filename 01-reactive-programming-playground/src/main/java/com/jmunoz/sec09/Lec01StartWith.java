package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

// Vamos a hacer ejemplos sencillos y en la próxima clase veremos ejemplos de la vida real para saber
// en qué casos se puede usar.
public class Lec01StartWith {

    private static final Logger log = LoggerFactory.getLogger(Lec01StartWith.class);

    public static void main(String[] args) {
        // demo1();
        // demo2();
        // demo3();
        demo4();

        // delayElements (en producer1()) usa un thread pool separado, por lo que
        // no bloquea el hilo principal. Lo bloqueamos nosotros para que
        // no termine el programa.
        Util.sleepSeconds(3);
    }

    private static void demo1() {
        // Antes de subscribirnos a producer1(), vamos a usar startWith para
        // agregar dos elementos al principio de la secuencia. Estos dos elementos
        // son los que se van a consumir primero.
        producer1()
                .startWith(Flux.just(-1, 0))
//                .take(2)        // No llega a invocar producer1 porque toma solo los elementos de startWith
                .subscribe(Util.subscriber());
    }

    private static void demo2() {
        producer1()
                .startWith(List.of(-2, -1, 0))
                .subscribe(Util.subscriber());
    }

    private static void demo3() {
        // Recordar que las peticiones van siempre de abajo a arriba.
        // En este caso, producer2() se ejecuta antes que producer1().
        // Y solo si el subscriber sigue queriendo más elementos (como en este ejemplo), se
        // invoca producer1()
        producer1()
                .startWith(producer2())
                .subscribe(Util.subscriber());
    }

    private static void demo4() {
        // Puede haber varios startWith y se ejecutan en orden de abajo a arriba.
        // El resultado de este ejemplo es: 49, 50, 51, 52, 53, 0, 1, 2, 3
        producer1()
                .startWith(0)
                .startWith(producer2())
                .startWith(49, 50)
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
