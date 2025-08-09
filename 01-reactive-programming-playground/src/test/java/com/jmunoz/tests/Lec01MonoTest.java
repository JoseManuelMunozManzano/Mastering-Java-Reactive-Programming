package com.jmunoz.tests;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// Escribir un test usando StepVerifier.
// StepVerifier actúa como un subscriber.
public class Lec01MonoTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01MonoTest.class);

    // Esta sería nuestra app.
    // Imaginemos que tenemos un Publisher.
    private Mono<String> getProduct(int id) {
        return Mono.fromSupplier(() -> "product-" + id)
                .doFirst(() -> log.info("invoked"));
    }

    // Este es nuestro test (es lo que tenemos que ejecutar)
    // Usamos StepVerifier.create, que acepta un publisher y tenemos que indicar qué esperamos.
    // Usamos expectNext() cuando tenemos claro que item esperamos del producer.
    // Usamos expectComplete() cuando esperamos que el producer devuelve la señal onComplete().
    //
    // Si no indicamos verify() el StepVerifier no se ejecutará, pero el test parecerá que ha pasado correctamente.
    // Sería muy parecido a indicar .subscribe() a un publisher en nuestra app.
    @Test
    public void productTest() {
        // Indicar el id 2 para que el test falle.
        StepVerifier.create(getProduct(1))
                .expectNext("product-1")
                .expectComplete()
                .verify();
    }
}
