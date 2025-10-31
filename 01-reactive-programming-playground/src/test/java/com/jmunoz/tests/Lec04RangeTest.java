package com.jmunoz.tests;

import com.jmunoz.common.Util;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Lec04RangeTest {

    // Esta sería nuestra app.
    // Imaginemos que tenemos un Publisher que emite muchos valores.
    private Flux<Integer> getItems() {
        return Flux.range(1, 50);
    }

    // Este producer genera valores aleatorios.
    private Flux<Integer> getRandomItems() {
        return Flux.range(1, 50)
                .map(i -> Util.faker().random().nextInt(1, 100));
    }

    // Si nuestro producer emite muchos items, no tiene sentido indicar cada valor en el méto-do expectNext()
    // Lo que hacemos es validar el valor de unos cuantos valores en el méto-do expectNext() y luego
    // indicamos la cantidad de veces que quedan por invocar del méto-do onNext().
    // Y, por último, esperamos del publisher la señal onComplete()
    @Test
    public void rangeTest1() {
        StepVerifier.create(getItems())
                .expectNext(1, 2, 3)
                .expectNextCount(47)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // En este ejemplo indicamos que esperamos del publisher los valores 1, 2 y 3.
    // Luego emitirá 22 items que nos dan igual.
    // Luego esperamos que emita los valores 26, 27 y 28.
    // Luego emitirá otros 22 items que nos vuelven a dar lo mismo.
    // Por último, esperamos que el publisher emita la señal onComplete()
    @Test
    public void rangeTest2() {
        StepVerifier.create(getItems())
                .expectNext(1, 2, 3)
                .expectNextCount(22)
                .expectNext(26, 27, 28)
                .expectNextCount(22)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // ¿Qué pasa si no sé qué valores me va a emitir el publisher? Es decir, si emite valores aleatorios.
    // No podemos usar expectNext() para validar, porque no sabemos el valor que vamos a obtener.
    // Podemos usar expectNextMatches() que acepta un Predicate que debe satisfacerse.
    // Pero solo se hace para el primer item emitido. Todavía nos quedan 49 items por recibir.
    // Tenemos varias opciones, como por ejemplo, usar expectNextCount()
    // para indicar el número de veces que esperamos que quede por invocar onNext()
    // Y por último, esperamos que el publisher emita la señal onComplete().
    @Test
    public void rangeTest3() {
        StepVerifier.create(getRandomItems())
                .expectNextMatches(i -> i > 0 && i < 101)
                .expectNextCount(49)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // Otra opción es usar el méto-do thenConsumeWhile() que acepta un Predicate en vez de expectNextMatches().
    // Lo que queremos decir es: nos subscribimos al producer y, sea cual sea el item que emita, tiene que pasar
    // la condición. De esta forma, no tenemos en cuenta valores concretos ni contamos las invocaciones faltantes
    // de onNext(), solo nos interesa que pase la condición indicada.
    @Test
    public void rangeTest4() {
        StepVerifier.create(getRandomItems())
                .thenConsumeWhile(i -> i > 0 && i < 101)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }
}
