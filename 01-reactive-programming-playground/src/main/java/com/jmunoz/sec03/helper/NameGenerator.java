package com.jmunoz.sec03.helper;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.IntStream;

public class NameGenerator {

    // Forma tradicional.
    public static List<String> getNamesList(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> generateName())
                .toList();
    }

    // Forma reactiva con Flux.
    public static Flux<String> getNamesFlux(int count) {
        return Flux.range(1, count)
                .map(i -> generateName());
    }

    private static String generateName() {
        // Simula un retraso de 1 segundo para cada nombre generado.
        Util.sleepSeconds(1);
        return Util.faker().name().firstName();
    }
}
