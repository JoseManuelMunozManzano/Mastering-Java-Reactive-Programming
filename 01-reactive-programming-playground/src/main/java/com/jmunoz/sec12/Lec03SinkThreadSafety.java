package com.jmunoz.sec12;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/*
  - Vamos a discutir si se puede compartir un `sink` con varios threads, es decir, si es Thread Safe.
  - Vamos a ver EmitFailureHandler.
*/
public class Lec03SinkThreadSafety {

    private static final Logger log = LoggerFactory.getLogger(Lec03SinkThreadSafety.class);

    public static void main(String[] args) {
//        demo1();
        demo2();
    }

    private static void demo1() {
        // A partir del cual emitiremos los items.
        // onBackPressureBuffer - unbounded queue
        var sink = Sinks.many().unicast().onBackpressureBuffer();

        // A partir del cual el subscriber recibirá los items.
        var flux = sink.asFlux();

        // Vamos a testear si sink es o no Thread Safe.
        // Vamos a trabajar con un ArrayList, que NO ES THREAD SAFE.
        // Elegido intencionalmente porque vamos a testear si obtenemos todos los items en nuestra lista.
        var list = new ArrayList<>();
        flux.subscribe(list::add);

        for (int i = 0; i < 1000; i++) {
            var j = i;
            CompletableFuture.runAsync(() -> {
                // Tenemos que usar una variable j para tryEmitNext porque
                // la variable tiene que ser final o effectively final.
                sink.tryEmitNext(j);
            });
        }

        // Bloqueamos el hilo principal.
        Util.sleepSeconds(2);

        // El tamaño de la lista debe ser de 1000, si to-do ha ido bien y es thread safe.
        log.info("List size: {}", list.size());

        // El resultado al que llegamos es que sink no es realmente thread safe usando tryEmitNext().
        //
        // Sink si es thread safe, pero el punto es que no está internamente sincronizado, es decir,
        // fallará si ve que varios threads intentan acceder (dentro del CompletableFuture del bucle for).
        //
        // Que Sink sea Thread Safe es responsabilidad nuestra, y para ello debemos usar emitNext() y EmitFailureHandler.
    }

    // Corrección del problema de demo1() usando emitNext() y EmitFailureHandler.
    private static void demo2() {
        var sink = Sinks.many().unicast().onBackpressureBuffer();
        var flux = sink.asFlux();

        // Vamos a trabajar con un ArrayList, que NO ES THREAD SAFE.
        // Elegido intencionalmente porque vamos a testear si obtenemos todos los items en nuestra lista.
        var list = new ArrayList<>();
        flux.subscribe(list::add);

        for (int i = 0; i < 1000; i++) {
            var j = i;
            CompletableFuture.runAsync(() -> {
                // Usamos emitNext en vez de tryEmitNext, y EmitFailureHandler.
                sink.emitNext(j, (signalType, emitResult) -> {
                    // Si hemos obtenido el valor, perfecto. Si no, lo reintenta.
                    return Sinks.EmitResult.FAIL_NON_SERIALIZED.equals(emitResult);
                });
            });
        }

        // Bloqueamos el hilo principal.
        Util.sleepSeconds(2);

        // El tamaño de la lista debe ser de 1000, si to-do ha ido bien y es thread safe.
        log.info("List size: {}", list.size());
    }
}
