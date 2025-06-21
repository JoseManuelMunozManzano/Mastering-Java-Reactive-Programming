package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Lec11FluxMono {

    public static void main(String[] args) {
        // A veces, debido a los requerimientos de la aplicación, puede ser necesario
        // transformar un Mono en un Flux o viceversa.

        // Mono a Flux
        monoToFlux();

        // Flux a Mono
        // Recordar que Mono solo puede emitir 0 o 1 elementos, no 10 como este Flux.
        // Para emitir un elemento, solo tenemos que usar el méto-do `next()`. Este méto-do devuelve un Mono.
        var flux = Flux.range(1, 10);
        flux.next()
                .subscribe(Util.subscriber());

        // Otra forma de convertir un Flux en un Mono es usar el méto-do `Mono.from()`.
        // Solo se emitirá el primer elemento del Flux.
        Mono.from(flux)
                .subscribe(Util.subscriber());
    }

    private static void monoToFlux() {
        // Imaginemos que queremos recuperar el nombre de un usuario a partir de su ID y luego grabarlo.
        // getUsername devuelve un Mono<String> y queremos grabarlo como un Flux<String>.
        //
        // Usamos el méotdo `Flux.from(Mono)` para convertir un Mono en un Flux.
        // Un Mono empty se convertirá en un Flux empty y un Mono error se convertirá en un Flux error.
        // Para probar: 1 es Ok, 2 es empty y 3 es error.
        var mono = getUsername(1);
        save(Flux.from(mono));
    }

    private static Mono<String> getUsername(int userId) {
        return switch (userId) {
            case 1 -> Mono.just("Sam");
            case 2 -> Mono.empty();
            default -> Mono.error(new RuntimeException("Invalid input"));
        };
    }

    private static void save(Flux<String> flux) {
        flux.subscribe(Util.subscriber());
    }
}
