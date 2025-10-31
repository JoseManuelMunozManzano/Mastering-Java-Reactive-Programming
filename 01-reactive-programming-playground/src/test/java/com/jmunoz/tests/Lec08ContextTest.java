package com.jmunoz.tests;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.util.context.Context;

// - Vemos como pasar el objeto `context` via test a nuestro producer, usando el méto-do withInitialContext()
public class Lec08ContextTest {

    // Esta sería nuestra app.
    // Imaginemos que tenemos este Publisher.
    private static Mono<String> getWelcomeMessage() {
        return Mono.deferContextual(ctx -> {
            // El context es un map.
            // Siguiendo con la nueva condición, vemos si en nuestro context tenemos un usuario autenticado.
            if (ctx.hasKey("user")) {
                // No olvidar devolver un publisher.
                return Mono.just("Welcome %s".formatted(ctx.get("user").toString()));
            }

            return Mono.error(new RuntimeException("unauthenticated"));
        });
    }

    // Validamos nuestro context creando un objeto StepVerifierOptions, y usando el
    // méto-do withInitialContext() al que le pasamos un mapa con nuestro context.
    //
    // Luego, indicamos lo que esperamos que nos emita el producer, pasándole la opción del
    // StepVerifierOptions.
    @Test
    public void welcomeMessageTest() {
        var options = StepVerifierOptions.create().withInitialContext(Context.of("user", "sam"));

        StepVerifier.create(getWelcomeMessage(), options)
                .expectNext("Welcome sam")
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // Validamos la condición else del context.
    // En esta condición else, el provider nos devuelve un error.
    @Test
    public void unauthenticatedTest() {
        var options = StepVerifierOptions.create().withInitialContext(Context.empty());

        // En este caso concreto, con un context.empty() podríamos no pasar options,
        // pero es mejor dejarlo explícito.
        StepVerifier.create(getWelcomeMessage(), options)
                .expectErrorMessage("unauthenticated")
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }
}
