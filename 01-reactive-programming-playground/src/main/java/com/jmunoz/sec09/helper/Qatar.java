package com.jmunoz.sec09.helper;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Qatar {

    private static final String AIRLINE = "Qatar";

    public static Flux<Flight> getFlights() {
        // En la vida real tendríamos una clase cliente con la que enviaríamos una petición HTTP a Qatar para
        // obtener la información de los vuelos.
        return Flux.range(1, Util.faker().random().nextInt(3, 5))
                .delayElements(Duration.ofMillis(Util.faker().random().nextInt(300, 800)))
                .map(i -> new Flight(AIRLINE, Util.faker().random().nextInt(400, 900)))
                .transform(Util.fluxLogger(AIRLINE));
    }
}
