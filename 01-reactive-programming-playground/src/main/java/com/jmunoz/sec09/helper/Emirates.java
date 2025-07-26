package com.jmunoz.sec09.helper;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Emirates {

    private static final String AIRLINE = "Emirates";

    public static Flux<Flight> getFlights() {
        // En la vida real tendríamos una clase cliente con la que enviaríamos una petición HTTP a Emirates para
        // obtener la información de los vuelos.
        return Flux.range(1, Util.faker().random().nextInt(2, 10))
                .delayElements(Duration.ofMillis(Util.faker().random().nextInt(200, 1000)))
                .map(i -> new Flight(AIRLINE, Util.faker().random().nextInt(300, 1000)))
                .transform(Util.fluxLogger(AIRLINE));
    }
}
