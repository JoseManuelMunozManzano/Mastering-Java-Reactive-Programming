package com.jmunoz.sec06;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

public class Lec01ColdPublisher {

    private static final Logger log = LoggerFactory.getLogger(Lec01ColdPublisher.class);

    public static void main(String[] args) {

        AtomicInteger atomicInteger = new AtomicInteger(0);

        var flux = Flux.create(fluxSink -> {
            log.info("invoked");
            for (int i = 0; i < 3; i++) {
                fluxSink.next(atomicInteger.incrementAndGet());
            }
            fluxSink.complete();
        });

        // Tenemos dos subscribers.
        // Cada subscriber obtiene sus datos de forma independiente.
        // Este es el comportamiento por defecto.
        // Ver ejemplo en package sec04, clase Lec02FluxCreateRefactor y la clase que usa NameGenerator.
        // ¿Por qué se supone que FluxSink es como un único suscriptor? No funcionó para múltiples suscriptores.
        // Porque FluxSink, su méto-do accept(), se ejecuta cada vez que se crea un nuevo suscriptor, y el último sobreescribe el anterior.
        flux.subscribe(Util.subscriber("sub1"));
        flux.subscribe(Util.subscriber("sub2"));
    }
}
