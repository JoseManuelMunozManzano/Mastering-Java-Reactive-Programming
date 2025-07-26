package com.jmunoz.sec09.helper;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class AmericanAirlanes {

    private static final String AIRLINE = "American Airlines";

    public static Flux<Flight> getFlights() {
        // En la vida real tendríamos una clase cliente con la que enviaríamos una petición HTTP a American Airlanes
        // para obtener la información de los vuelos.
        return Flux.range(1, Util.faker().random().nextInt(5, 10))
                .delayElements(Duration.ofMillis(Util.faker().random().nextInt(200, 1200)))
                .map(i -> new Flight(AIRLINE, Util.faker().random().nextInt(300, 1200)))
                .transform(Util.fluxLogger(AIRLINE));
    }
}
