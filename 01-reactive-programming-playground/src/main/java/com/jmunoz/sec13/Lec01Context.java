package com.jmunoz.sec13;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

// Se usa el context para proveer metadata sobre la petición (similar a los headers HTTP)
public class Lec01Context {

    private static final Logger log = LoggerFactory.getLogger(Lec01Context.class);

    public static void main(String[] args) {
        // El subscriber pasa la información del context, como si fuera un header de una petición HTTP.
        // Se usa el méto-do contextWrite() que acepta un Map donde el primer valor es la key y el segundo el value.
        // Conseguimos pasar información al publisher sin tener que actualizar la firma de parámetros del méto-do.
        getWelcomeMessage()
                .contextWrite(Context.of("user", "sam"))
                .subscribe(Util.subscriber());
    }

    // Imaginemos que tenemos un publisher a partir de una API remota.
    // Cualquiera puede subscribirse y obtener el mensaje.
    //
    // Pero cambian las condiciones y nos piden que solo usuarios autenticados puedan invocar este méto-do.
    // No queremos actualizar la firma de nuestro méto-do.
    // Para ello, Reactor provee el méto-do deferContextual() que acepta un objeto context del subscriber, y
    // podremos hacer lo que queramos con él, y devolvemos un publisher.
    private static Mono<String> getWelcomeMessage() {
        return Mono.deferContextual(ctx -> {
            // El context es un map.
            // Siguiendo con la nueva condición, vemos si es nuestro context tenemos un usuario autenticado.
            if (ctx.hasKey("user")) {
                // No olvidar devolver un publisher.
                return Mono.just("Welcome %s".formatted(ctx.get("user").toString()));
            }

            return Mono.error(new RuntimeException("unauthenticated"));
        });
    }
}
