package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import com.jmunoz.sec04.helper.NameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

public class Lec03FluxSinkThreadSafety {

    private static final Logger log = LoggerFactory.getLogger(Lec03FluxSinkThreadSafety.class);

    public static void main(String[] args) {
        // Antes de ver la demo de thread safety, vamos a ver un ejemplo de cómo no es thread safe.
        // El resultado deberían ser 10.000 items en la lista, pero vemos menos items porque no es
        // thread safe.
//        demo1();

        // Ejemplo con Flux Sink, que es thread safe.
        // El resultado son 10.000 items en la lista.
        // Esto es una demo, así no se hace en proyectos reales!!!!
        demo2();
    }

    private static void demo1() {
        // ArrayList no es thread safe, si dos hilos acceden a la misma instancia de ArrayList
        // habrá un bloqueo y uno no se grabará.
        var list = new ArrayList<Integer>();
        Runnable runnable = () -> {
            for (int i = 0; i < 1000; i++) {
                list.add(i);
            }
        };

        // Lanzamos 10 hilos.
        for (int i = 0; i < 10; i++) {
            Thread.ofPlatform().start(runnable);
        }

        // Bloqueamos el hilo principal para que el resto de hilos puedan hacer su trabajo.
        Util.sleepSeconds(3);
        log.info("List size: {}", list.size());
    }

    private static void demo2() {
        var list = new ArrayList<String>();

        var generator = new NameGenerator();
        var flux = Flux.create(generator);
        // Cuando obtengamos el item lo añadimos a la lista.
        // Obtendremos 10.000 items del provider.
        flux.subscribe(list::add);

        Runnable runnable = () -> {
            for (int i = 0; i < 1000; i++) {
                // Invocado 10.000 veces, ejecutado secuencialmente uno a uno, por eso es Thread Safe, aunque ArrayList no lo sea.
                generator.generate();
            }
        };

        // Lanzamos 10 hilos.
        for (int i = 0; i < 10; i++) {
            Thread.ofPlatform().start(runnable);
        }

        // Bloqueamos el hilo principal para que el resto de hilos puedan hacer su trabajo.
        Util.sleepSeconds(3);
        log.info("List size: {}", list.size());
    }
}
