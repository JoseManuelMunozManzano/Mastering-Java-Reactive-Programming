package com.jmunoz.tests;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

// Probar un publisher Flux es muy parecido a probar un publisher Mono, con alguna diferencia digna de mencionar.
public class Lec03FluxTest {

    // Esta sería nuestra app.
    // Imaginemos que tenemos un Publisher.
    private Flux<Integer> getItems() {
        return Flux.just(1, 2, 3)
                .log();
    }

    // Nuestro test, que es lo que realmente queremos ejecutar.
    // El producer nos da un objeto subscripción y, usando StepVerifier, por defecto la petición será por el valor Long.Max,
    // es decir, la petición es de to-do lo que emita el publisher.
    // Pero en el méto-do create(), podemos indicar como segundo parámetro el número de items requeridos.
    //
    // Solo espero un items, así que lo que ocurre después de create() es que espero el valor 1 expectNext(1)
    // Lo que ocurre después es que nos llegará la señal onComplete()
    //    PERO RESULTA QUE, como el producer puede emitir más de 1 item, que es lo que hemos pedido, no nos va a dar
    //    todavía la señal onComplete()
    // Lo que ocurre después realmente, es thenCancel(), es decir, que cancelamos.
    @Test
    public void fluxTest1() {
        StepVerifier.create(getItems(), 1)
                .expectNext(1)
                // ERROR, el publisher puede emitir 3 valores y nosotros solo requerimos 1. No emite señal onComplete()
//                .expectComplete()
                // Lo que tenemos que hacer es cancelar
                .thenCancel()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // En este test la petición es de todos los items, así que tras recibir todos los items, esperamos la señal
    // onComplete()
    // Pero notar que tenemos que indicar todos los items que esperamos.
    // El orden de los expectNext() es muy importante. Primero esperamos el valor 1, luego el 2 y por último el 3.
    @Test
    public void fluxTest2() {
        StepVerifier.create(getItems())
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // Vemos una forma resumida de indicar, usando expectNext() todos los valores esperados.
    // El orden sigue siendo muy importante.
    @Test
    public void fluxTest3() {
        StepVerifier.create(getItems())
                .expectNext(1, 2, 3)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // Recordar que usaremos expectNext() cuando tenemos claro el valor que esperamos.
    // Veremos en Lec04RangeTest como probar cuando nuestro producer emite un valor aleatorio.
}
