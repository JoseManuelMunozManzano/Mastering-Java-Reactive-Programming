package com.jmunoz.sec12;

import com.jmunoz.common.Util;
import reactor.core.publisher.Sinks;

// Podemos emitir múltiples mensajes, pero solo habrá un subscriber, que se puede unir más tarde si quiere.
// Los valores quedarán en memoria (unbounded queue), pero podemos poner limitaciones a la cola.
public class Lec02SinkUnicast {

    public static void main(String[] args) {
//        demo1();
        demo2();
    }

    private static void demo1() {
        // A partir del cual emitiremos los items.
        // onBackPressureBuffer - unbounded queue
        var sink = Sinks.many().unicast().onBackpressureBuffer();

        // A partir del cual el subscriber recibirá los items.
        var flux = sink.asFlux();

        // Emitimos los items.
        // Tenemos a nuestra disposición los méto-dos:
        //   tryEmitNext, tryEmitComplete, tryEmitError
        //   emitNext, emitComplete, emitError
        // Vemos que onComplete no se va a emitir a no ser que lo indiquemos.
        sink.tryEmitNext("hi");
        sink.tryEmitNext("how are you");
        sink.tryEmitNext("?");

        // Nos subscribimos (unicast solo permite uno)
        flux.subscribe(Util.subscriber("sam"));
    }

    private static void demo2() {
        // A partir del cual emitiremos los items.
        // onBackPressureBuffer - unbounded queue
        var sink = Sinks.many().unicast().onBackpressureBuffer();

        // A partir del cual el subscriber recibirá los items.
        var flux = sink.asFlux();

        sink.tryEmitNext("hi");
        sink.tryEmitNext("how are you");
        sink.tryEmitNext("?");

        flux.subscribe(Util.subscriber("sam"));

        // ¿Qué ocurre si añado otro subscriber?
        // El primer subscriber recibe la data y el segundo emite la excepción IllegalStateException.
        flux.subscribe(Util.subscriber("mike"));
    }
}
