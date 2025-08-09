package com.jmunoz.tests;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

// Vamos a ver como probar que un test se complete en una duración específica.
public class Lec10TimeoutTest {

    // Esta sería nuestra app.
    // Imaginemos que tenemos un Publisher que emite varios valores, pero cada item se emite tras una espera de 200 ms
    private Flux<Integer> getItems() {
        return Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));
    }

    // Tenemos como requerimiento que el test se complete en 500 ms.
    // Para conseguirlo, el méto-do verify() acepta una Duration.
    @Test
    public void timeoutTest() {
        StepVerifier.create(getItems())
                .expectNext(1, 2, 3, 4, 5)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                // Notar que acepta una Duration. Es el tiempo máximo en el que se debe completar este test.
                // Si no se cumple este tiempo máximo, el test falla (En este ejemplo falla)
                // Indicar 1500 para que el test pase.
                .verify(Duration.ofMillis(500));
    }
}
