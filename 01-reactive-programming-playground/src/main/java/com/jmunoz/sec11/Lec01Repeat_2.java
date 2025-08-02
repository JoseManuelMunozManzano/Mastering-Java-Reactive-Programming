package com.jmunoz.sec11;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

// El operador repeat se resubscribe cuando aparece la señal onComplete()
// No hace nada si la señal es onError()
public class Lec01Repeat_2 {

    public static void main(String[] args) {
//        demo1();

//        demo2();

//        demo3();

        // Para demo4() bloqueamos el hilo principal por algunos segundos.
        demo4();
        Util.sleepSeconds(10);

//        demo5();
    }

    // Este es el mismo ejemplo que hemos visto en Lec01Repeat.java, pero refactorizado.
    private static void demo1() {
        getCountryName()
                .repeat(3)
                .subscribe(Util.subscriber());
    }

    // En este ejemplo, repetimos de forma indefinida, pero paramos cuando obtenemos el valor que queramos.
    private static void demo2() {
        getCountryName()
                .repeat()
                .takeUntil(c -> c.equalsIgnoreCase("canada"))
                .subscribe(Util.subscriber());
    }

    // El operador repeat() puede aceptar un Supplier booleano (no exactamente un predicate, pero parecido) para
    // tener una especie de condición. Mientras se cumpla esta condición, se ejecutará el operador repeat().
    private static void demo3() {
        var atomicInteger = new AtomicInteger(0);

        getCountryName()
                .repeat(() -> atomicInteger.incrementAndGet() < 3)
                .subscribe(Util.subscriber());
    }

    // En vez de ejecutar repeat() inmediatamente, lo ejecutamos en intervalos de tiempo, evitando bombardear
    // con peticiones. Usamos repeatWhen() y notar que usamos un Flux.
    // También podemos indicar, además del intervalo, una cantidad de veces que queremos que se ejecute repeat().
    private static void demo4() {
        getCountryName()
                .repeatWhen(flux -> flux.delayElements(Duration.ofSeconds(2)).take(2))
                .subscribe(Util.subscriber());
    }

    // Hasta ahora hemos usado Mono, pero podemos hacer un repeat de un Flux perfectamente.
    private static void demo5() {
        Flux.just(1, 2, 3)
                .repeat(3)
                .subscribe(Util.subscriber());
    }

    private static Mono<String> getCountryName() {
        return Mono.fromSupplier(() -> Util.faker().country().name());      // non-blocking IO
    }
}
