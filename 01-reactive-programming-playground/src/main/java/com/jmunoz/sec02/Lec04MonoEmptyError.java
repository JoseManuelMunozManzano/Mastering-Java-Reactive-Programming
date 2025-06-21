package com.jmunoz.sec02;

import com.jmunoz.common.Util;
import reactor.core.publisher.Mono;

// Vemos como crear un publisher que no va a dar data y que pasa un mensaje de error.
public class Lec04MonoEmptyError {

    public static void main(String[] args) {

        // Probar con valores 1, 2 y 50
        getUsername(1)
                .subscribe(Util.subscriber());

        // On Error Dropped - Problem
        // Estamos usando un Consumer como subscriber al que indicamos solo onNext().
        // Como el publisher nos ha dado un mensaje de error y aquí no lo estamos manejando,
        // da el error Operator called default onErrorDropped.
        getUsername(50)
                .subscribe(
                        s -> System.out.println(s),
                        // Corregimos añadiendo onError(), aunque sea sin nada, para que no falle.
                        err -> {}
                );
    }

    // En programación reactiva no hay null, se usa Mono.empty()
    private static Mono<String> getUsername(int userId) {
        return switch (userId) {
            case 1 -> Mono.just("Sam");
            case 2 -> Mono.empty();
            default -> Mono.error(new RuntimeException("Invalid input"));
        };
    }
}
