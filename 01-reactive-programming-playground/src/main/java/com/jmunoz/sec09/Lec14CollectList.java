package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// Para recoger los items recibidos via Flux. Asumimos que tenemos items finitos.
public class Lec14CollectList {

    public static void main(String[] args) {
        // Una vez usado collectList, el resultado ya no es un Flux, sino un Mono<List<T>>.
        // Y al subscribirnos, recibiremos una lista con todos los items emitidos por el Flux.
        // No es bloqueante.
        Flux.range(1, 10)
                .collectList()
                .subscribe(Util.subscriber());

        // En caso de error no obtendremos la lista, solo el error.
        Flux.range(1, 10)
                .concatWith(Mono.error(new RuntimeException("oops")))
                .collectList()
                .subscribe(Util.subscriber());
    }
}
