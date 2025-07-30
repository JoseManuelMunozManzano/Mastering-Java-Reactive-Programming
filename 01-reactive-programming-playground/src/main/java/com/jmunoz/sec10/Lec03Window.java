package com.jmunoz.sec10;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class Lec03Window {

    public static void main(String[] args) {
        // Podemos indicar una cantidad de eventos o un periodo de tiempo.
        // Vemos que window() devuelve un Flux<Flux<String>> porque como ya se ha dicho
        // cada x items o cada periodo de tiempo indicado se abre un nuevo Flux.
        //
        // Por tanto, alguien tiene que subscribirse al Flux interno.
        // Sin embargo, no funciona flatMap() porque, si lo usamos, estamos abriendo y cerrando a la vez ese window.
        // Es como si no hubiéramos indicado window().
        // El subscriber es un nuevo fichero, ya que cada x elementos o cada x tiempo, se abre un nuevo fichero log.
        // Por tanto, el fichero va cambiando.
        eventStream()
                .window(5)
                // .flatMap(flux -> flux)  // Esto no funciona
                .flatMap(Lec03Window::processEvents)
                .subscribe();

        // Para dar tiempo a que termine el programa.
        Util.sleepSeconds(60);
    }

    private static Flux<String> eventStream() {
        return Flux.interval(Duration.ofMillis(500))
                .map(i -> "event-" + (i + 1));
    }

    // Este méto-do es el que actúa como subscriber para el Flux interno.
    // Solo vamos a imprimir *, uno por cada elemento, y cuando termine de escribir los 5 elementos,
    // se pasa a la siguiente ventana de 5 elementos.
    private static Mono<Void> processEvents(Flux<String> flux) {
        return flux.doOnNext(e -> System.out.print("*"))
                .doOnComplete(System.out::println)
                .then();    // No lo queremos enviar a nada más. Solo esperamos al onComplete.
    }
}
