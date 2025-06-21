package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

public class Lec10FluxEmptyError {

    public static void main(String[] args) {

        // Un subscriber pide mensajes a un publisher, pero este devuelve empty porque no tiene nada que emitir.
        // Veremos en consola onComplete.
        Flux.empty()
                .subscribe(Util.subscriber());

        // También puede haber ocurrido un error, pasando un throwable al subscriber.
        // Veremos en consola onError.
        Flux.error(new RuntimeException("oops"))
                .subscribe(Util.subscriber());
    }
}
