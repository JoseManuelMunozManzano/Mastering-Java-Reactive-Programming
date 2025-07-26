package com.jmunoz.sec09.helper;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class NameGenerator {

    private static final Logger log = LoggerFactory.getLogger(NameGenerator.class);
    private final List<String> redis = new ArrayList<>();   // Para demo.

    public Flux<String> generateNames() {
        // Como este proceso consume mucho tiempo,
        // podemos almacenar los nombres en caché para leerlos de ahí y ahorrar tiempo de computación.
        // Aquí usamos una lista, pero en la vida real podríamos usar Redis.
        // Notar el uso de startWith para empezar con los nombres que ya tenemos en caché.

        return Flux.generate(sink -> {
                    log.info("generating name"); // Para saber cuando se invoca el generador.
                    Util.sleepSeconds(1); // Simulamos que se consume mucho tiempo.
                    var name = Util.faker().name().firstName();
                    redis.add(name);
                    sink.next(name);
                })
                .startWith(redis) // Empezamos con los nombres que ya tenemos en caché (en la lista redis).
                .cast(String.class);
    }
}
