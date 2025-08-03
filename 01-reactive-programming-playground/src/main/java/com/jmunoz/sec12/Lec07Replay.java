package com.jmunoz.sec12;

import com.jmunoz.common.Util;
import reactor.core.publisher.Sinks;

/*
    - `Sinks.many().replay();` es un publisher tipo Flux en el que varios subscribers pueden subscribirse.
    - El que subscriba tarde sigue obteniendo todos los mensajes.
 */
public class Lec07Replay {

    public static void main(String[] args) {
        demo1();
    }

    private static void demo1() {
        // A partir del cual emitiremos los items.
        // Indicando all(), intentaremos almacenar todos los mensajes en un unbounded queue.
        // Indicando limit(), el que se subscriba más tarde, solo obtendrá los mensajes emitidos en el último x Duration,
        //   o los últimos x items emitidos... empezando desde el final.
        //   En este ejemplo, jake obtendrá solo "?", por limit(1).
        //
        var sink = Sinks.many().replay().all();
//        var sink = Sinks.many().replay().limit(1);

        // A partir del cual los subscribers recibirán los items.
        var flux = sink.asFlux();

        flux.subscribe(Util.subscriber("sam"));
        flux.subscribe(Util.subscriber("mike"));

        sink.tryEmitNext("hi");
        sink.tryEmitNext("how are you");
        sink.tryEmitNext("?");

        // Veremos como jake obtiene los tres mensajes que ya se han emitido, aunque se subscriba más tarde.
        // Por supuesto, todos los subscribers obtienen el siguiente mensaje.
        Util.sleepSeconds(2);
        flux.subscribe(Util.subscriber("jake"));
        sink.tryEmitNext("new message");
    }
}
