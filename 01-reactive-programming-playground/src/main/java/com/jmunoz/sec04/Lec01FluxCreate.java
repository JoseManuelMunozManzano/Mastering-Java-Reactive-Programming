package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

// Crear Flux y emitir items programáticamente.
public class Lec01FluxCreate {

    public static void main(String[] args) {

        // Flux.create() acepta un Consumer que recibe un FluxSink.
        // Vemos como emitir items de forma programática en un bucle for y con condiciones.
        Flux.create(fluxSink -> {
            for (int i = 0; i < 10; i++) {
                fluxSink.next(Util.faker().country().name());
            }

            System.out.println("Emitiendo de nuevo");

            String country;
            do {
                country = Util.faker().country().name();
                fluxSink.next(country);
            } while (!country.equalsIgnoreCase("canada"));

            fluxSink.complete();
        })
                .subscribe(Util.subscriber());
    }
}
