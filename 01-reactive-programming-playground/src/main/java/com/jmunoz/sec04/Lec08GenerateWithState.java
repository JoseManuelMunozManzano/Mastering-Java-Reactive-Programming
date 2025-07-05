package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

// Vemos que Flux.generate() es sin estado.
// Cuando queremos mutar un estado dentro de synchronousSink, podemos encontrarnos con problemas.
// Vemos el problema y una solución no óptima.
public class Lec08GenerateWithState {

    public static void main(String[] args) {
        // PETICIÓN: Paramos cuando encontremos el país Canada, o cuando se emitan como mucho 10 países.
        //
        // Se podría hacer con un operador, pero como producer, deberíamos poder parar la emisión cuando queramos.
        // No podemos usar un contador, porque cada vez que se invoca SynchronousSink, este se reinicia.
        //
        // Una forma sería usar un AtomicInteger, pero vemos que su declaración queda fuera de Flux.generate,
        // lo que podría ser un problema si alguien modifica el valor de atomicInteger desde fuera del flujo.

        AtomicInteger atomicInteger = new AtomicInteger(0);

        Flux.generate(synchronousSink -> {
                    String country = Util.faker().country().name();
                    synchronousSink.next(country);
                    atomicInteger.incrementAndGet();

                    if (atomicInteger.get() == 10 || country.equalsIgnoreCase("canada")) {
                        synchronousSink.complete();
                    }
                })
                .subscribe(Util.subscriber());
    }
}
