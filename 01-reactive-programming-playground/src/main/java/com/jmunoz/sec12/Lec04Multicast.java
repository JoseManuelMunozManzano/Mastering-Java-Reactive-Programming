package com.jmunoz.sec12;

import com.jmunoz.common.Util;
import reactor.core.publisher.Sinks;

/*
    - `Sinks.many().replay();` es un publisher tipo Flux en el que varios subscribers pueden subscribirse.
    - El que subscriba tarde pierde la data que ya se haya emitido.
*/
public class Lec04Multicast {

    public static void main(String[] args) {
//        demo1();
        demo2();
    }

    private static void demo1() {
        // A partir del cual emitiremos los items.
        // onBackPressureBuffer - bounded queue (la cola como máx. es de 256, y puede tener valores 1, 8, 16...)
        var sink = Sinks.many().multicast().onBackpressureBuffer();

        // A partir del cual los subscribers recibirán los items.
        var flux = sink.asFlux();

        flux.subscribe(Util.subscriber("sam"));
        flux.subscribe(Util.subscriber("mike"));

        sink.tryEmitNext("hi");
        sink.tryEmitNext("how are you");
        sink.tryEmitNext("?");

        // Veremos como jake se pierde la emisión de los tres primeros items, ya que
        // se subscribe más tarde.
        Util.sleepSeconds(2);
        flux.subscribe(Util.subscriber("jake"));
        sink.tryEmitNext("new message");
    }

    // Si los subscribers que se subscriben tarde no reciben los mensajes anteriores,
    // ¿qué sentido tiene el buffer? multicast() tiene un comportamiento interesante llamado
    // comportamiento warmup
    private static void demo2() {
        // A partir del cual emitiremos los items.
        // onBackPressureBuffer - bounded queue (la cola como máx. es de 256, y puede tener valores 1, 8, 16...)
        var sink = Sinks.many().multicast().onBackpressureBuffer();

        // A partir del cual los subscribers recibirán los items.
        var flux = sink.asFlux();

        // Intentamos emitir sin tener subscribers.
        // La forma en que funciona multicast, es que, como no hay subscribers, estos mensajes
        // van a la cola. Si la cola se llena, los mensajes se descartan.
        sink.tryEmitNext("hi");
        sink.tryEmitNext("how are you");
        sink.tryEmitNext("?");

        Util.sleepSeconds(2);

        // Tras 2 segundos, se subscriben 3 a la vez.
        // Vemos que solo el primer subscriptor (sam) recibe los mensajes anteriores que están en la cola.
        flux.subscribe(Util.subscriber("sam"));
        flux.subscribe(Util.subscriber("mike"));
        flux.subscribe(Util.subscriber("jake"));

        // Emitimos un último mensaje, que reciben todos los suscritos.
        sink.tryEmitNext("new message");
    }
}
