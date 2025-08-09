package com.jmunoz.tests;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Lec06VirtualTimeTest {

    // Esta sería nuestra app.
    // Imaginemos que tenemos un Publisher que emite varios valores, pero cada item se emite tras una espera de 10sg
    private Flux<Integer> getItems() {
        return Flux.range(1, 5)
                .delayElements(Duration.ofSeconds(10));
    }

    // Como cada item se emite tras una espera de 10sg, testearlo tarda 50sg!!
    // Este test tarda muchísimo, no tiene sentido!!!!
//    @Test
    public void rangeTest1() {
        StepVerifier.create(getItems())
                .expectNext(1, 2, 3, 4, 5)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // Uso de withVirtualTime() para poder testear producers que tardan mucho en emitir sus valores.
    // Hay que pasarle un Supplier.
    // Luego usamos thenAwait(), al que le pasamos una Duration. Este Duration es un tiempo que simulamos
    // que YA HA PASADO.
    // Es decir, si ya han pasado 51sg, lo que espero es tener los 5 valores que emite el producer.
    // Y luego espero obtener la señal onComplete().
    //
    // NOTA: Si simulamos que han pasado 45sg, tendremos que esperar 5sg hasta obtener el resultado.
    @Test
    public void virtualTimeTest1() {
        StepVerifier.withVirtualTime(this::getItems)
                .thenAwait(Duration.ofSeconds(51))
                .expectNext(1, 2, 3, 4, 5)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // Aunque el ejemplo anterior es perfectamente válido, a veces podemos tener diferentes requerimientos.
    //
    // El primer evento se emite tras pasar 10sg, ¿cómo puedo validar esto?
    // Usamos el méto-do expectNoEvent() al que se le pasa un Duration, por ejemplo 9sg, para indicar
    // que no se emiten eventos en ese tiempo.
    // Luego usamos thenAwait() para esperar 1sg.
    // Obtendremos 1 item.
    // Luego, podemos esperar, usando de nuevo thenAwait(), 40sg
    // Obtendremos el resto de items.
    // Y luego espero obtener la señal onComplete().
    //
    // Este enfoque tiene UN PROBLEMA: Indicamos que durante 9sg no se reciben eventos, pero SI SE RECIBE UNO,
    // Se recibe el evento onSubscribe.
    //
    // Para corregir este problema, se ha añadido expectSubscription()
    // Este méto-do está implícito (no ha hecho falta indicarlo en ningún otro test), pero en este caso sí que hay
    // que indicarlo explícitamente, porque estamos indicando que no esperamos eventos del publisher.
    @Test
    public void virtualTimeTest2() {
        StepVerifier.withVirtualTime(this::getItems)
                // Si no se informa este méto-do, el test falla, porque este evento SI que lo recibimos.
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(9))
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(1)
                .thenAwait(Duration.ofSeconds(40))
                .expectNext(2, 3, 4, 5)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }
}
