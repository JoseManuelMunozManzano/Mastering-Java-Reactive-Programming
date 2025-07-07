package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/*
    Similar a manejo de errores, onErrorResume
    En caso de empty, llama a otro publisher.
*/
public class Lec08SwitchIfEmpty {

    private static final Logger log = LoggerFactory.getLogger(Lec08SwitchIfEmpty.class);

    public static void main(String[] args) {

        // Solo si el publisher es empty, se ejecuta el fallback, como en este caso, que el filtro hace
        // que no haya elementos.
        // Un caso de ejemplo real sería con una BD Postgres + BD Redis (caché)
        // Podemos buscar el nombre del producto en caché (Redis) y si es empty, buscarlo en la BD (Postgres).
        Flux.range(1, 10)
                .filter(i -> i > 11)
                .switchIfEmpty(fallback())
                .subscribe(Util.subscriber());

    }

    // Fallback producer (o publisher, recordar que es lo mismo)
    private static Flux<Integer> fallback() {
        return Flux.range(100, 3);
    }
}
