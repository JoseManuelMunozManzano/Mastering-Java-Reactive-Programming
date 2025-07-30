package com.jmunoz.sec10;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class Lec05GroupedFlux {

    private static final Logger log = LoggerFactory.getLogger(Lec05GroupedFlux.class);

    // Agrupamos basado en si el número es par o impar.
    // Veremos que groupBy devuelve un Flux<GroupedFlux<K, T>>, es decir, tiene Flux internos.
    // GroupedFlux usa una key, por la que se puede acceder para realizar procesamientos específicos.
    public static void main(String[] args) {

        Flux.range(1, 5)
                .delayElements(Duration.ofSeconds(1))
                .map(i -> i * 2)     // Con esto nunca tendremos un Flux de impares.
                .startWith(1)       // Con esto tendremos un solo impar al inicio, se crea un Flux de impares, pero NO SE CIERRA hasta que se complete la fuente de datos.
                .groupBy(i -> i % 2) // 0, 1
                .flatMap(Lec05GroupedFlux::processEvents)
                .subscribe();

        // Bloqueamos el hilo principal.
        Util.sleepSeconds(60);
    }

    // La key es integer y el valor es también un integer.
    private static Mono<Void> processEvents(GroupedFlux<Integer, Integer> groupedFlux) {
        log.info("received flux for {}", groupedFlux.key());
        return groupedFlux.doOnNext(i -> log.info("key: {}, value: {}", groupedFlux.key(), i))
                // No se ejecuta porque el Flux no se cierra nunca (no sabemos si obtendremos valores impares en el futuro) hasta que se termina la fuente de datos.
                // Tenemos que tener cuidado de cuantos Flux creamos (sobre todo si la fuente de datos es infinita).
                .doOnComplete(() -> log.info("{} completed", groupedFlux.key()))
                .then(); // No devolvemos nada, solo procesamos.
    }
}
