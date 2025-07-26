package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.time.Duration;

/*
    Zip
        - Nos subscribimos a todos los producers a la vez.
        - Es to-do o nada.
        - Todos los producers tienen que emitir un valor.
*/
public class Lec07Zip {

    record Car(String body, String engine, String tires) {}

    public static void main(String[] args) {
        // zip() trabaja con Tuplas de distintos tipos de datos.
        // En este caso solo hay un tipo de dato, String, pero los producers pueden ser de distintos tipos.
        // Internamente, podemos acceder a cada uno, por ejemplo con un map(), indicando getT1(), getT2() y getT3(),
        // para montar el objeto Car.
        //
        // El resultado de nuestro ejemplo muestra que, aunque puedo emitir hasta 10 ruedas, solo puedo montar 3 coches
        // porque solo tengo 3 motores.
        Flux.zip(getBody(), getEngine(), getTires())
                .map(t -> new Car(t.getT1(), t.getT2(), t.getT3()))
                .subscribe(Util.subscriber());

        // Bloqueamos el hilo principal para que no se cierre antes de que los publishers terminen.
        Util.sleepSeconds(5);
    }

    // Estos publishers montan un objeto coche, pero cada uno emite a diferentes velocidades.
    // Hasta que no emiten todos un valor, no podemos montar el objeto coche.
    private static Flux<String> getBody() {
        return Flux.range(1, 5)
                .map(i -> "body-" + i)
                .delayElements(Duration.ofMillis(100));
    }

    private static Flux<String> getEngine() {
        return Flux.range(1, 3)
                .map(i -> "engine-" + i)
                .delayElements(Duration.ofMillis(200));
    }

    private static Flux<String> getTires() {
        return Flux.range(1, 10)
                .map(i -> "tires-" + i)
                .delayElements(Duration.ofMillis(79));
    }
}
