package com.jmunoz.tests;

import com.jmunoz.common.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Objects;

/*
    assertNext() es un méto-do de StepVerifier.
    assertNext() = consumeNextWith()
    También podemos recoger todos los items y probar, usando collectAll()
*/
public class Lec05AssertNextTest {

    record Book(int id, String author, String title) {}

    // Esta sería nuestra app.
    // Imaginemos que tenemos un Publisher que emite varios objetos Book.
    private Flux<Book> getBooks() {
        return Flux.range(1, 3)
                .map(i -> new Book(i, Util.faker().book().author(), Util.faker().book().title()));
    }

    // Usamos el méto-do assertNext(), al que hay que pasarle un Consumer.
    // Para el primer item, indicamos que esperamos el id 1.
    // Luego testeamos el resto de items, indicando que title vendrá informado.
    // Por último esperamos que el publisher emita la señal onComplete().
    @Test
    public void assertNextTest() {
        StepVerifier.create(getBooks())
                .assertNext(b -> Assertions.assertEquals(1, b.id))
                .thenConsumeWhile(b -> Objects.nonNull(b.title))
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // Usamos el méto-do collectAll()
    // Lo que hacemos, en vez de probar la recuperación de items concretos, es recoger todos
    // los items, por ejemplo a una lista.
    // Tener en cuenta que ya NO ES UN FLUX, sino un Mono<List<Books>>
    // Ahora en assertNext() lo que tenemos es una lista, y podemos añadir nuestras propias aserciones para validar
    // la lista completa.
    // Por ejemplo, en este caso, validamos que la lista contiene 3 elementos.
    // Como es un Mono, solo tenemos un item.
    // Por último esperamos que el publisher emita la señal onComplete().
    @Test
    public void collectAllAndTest() {
        StepVerifier.create(getBooks().collectList())
                .assertNext(list -> Assertions.assertEquals(3, list.size()))
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }
}
