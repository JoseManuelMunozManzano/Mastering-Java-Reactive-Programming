package com.jmunoz.tests;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.util.function.UnaryOperator;

// Vemos como hacer que los tests actúen como producer.
public class Lec09PublisherTest {

    // Imaginemos que en nuestra app tenemos complejas reglas de negocio.
    private UnaryOperator<Flux<String>> processor() {
        return flux -> flux
                .filter(s -> s.length() > 1)
                .map(String::toUpperCase)
                .map(s -> s + ":" + s.length());
    }

    // Para probar reglas de negocio usando un publisher, Reactor provee una utilidad llamada TestPublisher.
    // Devuelve un tipo TestPublisher.
    // TestPublisher dispone de varios méto-dos muy útiles para escribir unit tests.
    //
    // Nuestra app está esperando un Flux o un Mono, por lo que usaremos el méto-do flux() o mono().
    // Ahora, podemos pasar la instancia flux a nuestra app para que la app pueda recibir los valores.
    // Y, en esta clase de test, usando la instancia publisher, podemos emitir data.
    @Test
    public void publisherTest1() {
        var publisher = TestPublisher.<String>create();
        var flux = publisher.flux();

        // -------------- TEORIA ----------------------
        // Usamos flux para subscribirnos.
//        flux.subscribe(Util.subscriber());

        // Usamos publisher para emitir data.
        // Emitimos la señal complete() (también podemos emitir la señal error() si es lo que queremos)
//        publisher.next("a", "b");
//        publisher.complete();

        // Pero, en vez de emitir la data como se ve arriba, hay otro méto-do emit(), más útil, porque
        // ya emite la señal complete() automáticamente.
//        publisher.emit("a", "b");
        // -------------- FIN TEORIA ----------------------

        // La data que queremos emitir tiene que estar integrada en el test, usando el méto-do then() y pasándole
        // en un Runnable la data que queremos emitir.
        // En la teoría indicada arriba, esta sentencia funcionaba porque teníamos un subscriptor
        // al flux y LUEGO emitíamos. Pero aquí to-do ocurre como parte del test y este emit aparte no funciona.
//        publisher.emit("hi", "hello");

        // Para hacer el testing a nuestra app (méto-do processor)
        // Como se ha indicado, usamos el objeto flux para pasar la data emitida a processor()
        // La data se emite en el méto-do then(), al que tenemos que pasar un Runnable con la data a emitir.
        //
        // Es importante volver a indicar que el test se subscribe, como se ha dicho antes, al objeto flux (usando verify())
        // Y los valores los emite el objeto publisher.
        StepVerifier.create(flux.transform(processor()))
                .then(() -> publisher.emit("hi", "hello"))
                .expectNext("HI:2")
                .expectNext("HELLO:5")
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // Este otro test también pasa porque el méto-do processor() filtra lo que no sea String.
    // Como no le pasamos ningún item a processor(), al final se completa (este si lo pasamos)
    // y el test funciona.
    @Test
    public void publisherTest2() {
        var publisher = TestPublisher.<String>create();
        var flux = publisher.flux();

        StepVerifier.create(flux.transform(processor()))
                .then(() -> publisher.emit("a", "b", "c"))
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }
}
