package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/*
    Similar a manejo de errores, onErrorReturn.
    Manejo de empty cuando el producer no emite ningún valor.
*/
public class Lec07DefaultIfEmpty {

    public static void main(String[] args) {
        // Similar a Optional.empty(), cuando existe orElse() para devolver un valor por defecto.
        // Optional.empty().orElse("Default Value");

        Mono.empty()
                .defaultIfEmpty("fallback")
                .subscribe(Util.subscriber());

        // Si el producer emite un valor, se usa ese valor, NO ES EMPTY.
        Mono.just("jose")
                .defaultIfEmpty("fallback")
                .subscribe(Util.subscriber());

        // En Flux funciona igual.
        // Se emiten valores, pero un operator los filtra. ES EMPTY desde el punto de vista del subscriber.
        Flux.range(1, 10)
                .filter(i -> i > 11)
                .defaultIfEmpty(50)
                .subscribe(Util.subscriber());

        // Si un valor emitido llega al subscriber, se usa ese valor, NO ES EMPTY.
        Flux.range(1, 10)
                .filter(i -> i > 9)
                .defaultIfEmpty(50)
                .subscribe(Util.subscriber());
    }
}
