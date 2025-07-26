package com.jmunoz.sec09.helper;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class Kayak {

    public static Flux<Flight> getFlights() {
        // Hacemos un merge de los vuelos de las aerolíneas y esperaremos como mucho 2sg.
        return Flux.merge(
                        AmericanAirlanes.getFlights(),
                        Emirates.getFlights(),
                        Qatar.getFlights()
                )
                .take(Duration.ofSeconds(2));
    }
}
