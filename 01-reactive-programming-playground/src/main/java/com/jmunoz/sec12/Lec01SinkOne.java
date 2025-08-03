package com.jmunoz.sec12;


import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

// Una forma rápida de ver los sink es que es parecido a un Flux.create(), que ya aceptaba un sink, pero sin
// necesidad de un subscriber.
//
// Vemos Sinks.one(), que nos permite crear un sink con el que podemos emitir un máximo de 1 item.
// Es un tipo Mono Sink.
// Para emitir valores, utilizamos el méto-do sink.emitValue() o sink.tryEmitValue()
// Para indicar que no tenemos data, usamos el méto-do sink.tryEmitEmpty()
// Para mandar señal de error, usamos el méto-do sink.tryEmitError()
// Para obtener la data, utilizamos el méto-do sink.asMono()
public class Lec01SinkOne {

    private static final Logger log = LoggerFactory.getLogger(Lec01SinkOne.class);

    public static void main(String[] args) {

//        demo1();

//        demo2();

        demo3();
    }

    // Explorando méto-dos sink para emitir item / empty / error
    private static void demo1() {
        // Creamos un tipo Mono Sink.
        Sinks.One<Object> sink = Sinks.one();

        // Creamos un mono a partir del Sink.
        Mono<Object> mono = sink.asMono();

        // Como tenemos un mono, podemos subscribirnos.
        mono.subscribe(Util.subscriber());

        // Intentamos emitir data (es un mono, como mucho un valor).
//        sink.tryEmitValue("hi");

        // Indicamos que no hay data que emitir.
//        sink.tryEmitEmpty();

        // Enviamos señal de error.
        sink.tryEmitError(new RuntimeException("oops!"));
    }

    private static void demo2() {
        Sinks.One<Object> sink = Sinks.one();
        Mono<Object> mono = sink.asMono();

        // Como no necesitamos subscribers cuando emitimos un valor,
        // nos podemos traer la emisión aquí.
        sink.tryEmitValue("hi");

        // Podemos tener múltiples subscribers.
        mono.subscribe(Util.subscriber("sam"));
        mono.subscribe(Util.subscriber("mike"));
    }

    private static void demo3() {
        Sinks.One<Object> sink = Sinks.one();
        Mono<Object> mono = sink.asMono();

        mono.subscribe(Util.subscriber("sam"));

        // Vemos el méto-do sink.emitValue().
        // Acepta un EmitFailureHandler, que es un interface que solo tiene un méto-do (podemos usar expresiones lambda)
        // El méto-do acepta signalType y emitResult
        // y devuelve un booleano, donde indicamos si queremos reintentar.
        //
        // Esta emisión funciona y se envía sin problemas (es un Mono) y devuelve la señal onComplete(),
        // es decir, no ejecuta la parte del EmitFailureHandler.
        sink.emitValue("hi", (signalType, emitResult) -> {
            log.info("hi");
            log.info(signalType.name());
            log.info(emitResult.name());
            return false;
        });

        // Esta emisión falla porque ya emitimos la señal onComplete() (de nuevo, es un Mono).
        sink.emitValue("hello", (signalType, emitResult) -> {
            log.info("hello");
            log.info(signalType.name());
            log.info(emitResult.name());
            return false;
        });

        // La diferencia entre emitValue() y tryEmitValue() es que el segundo intenta emitir un valor
        // y si no puede, no nos lo notificará e ignorará el problema.
        // Con emitValue() nos queremos asegurar de que el valor se envía, y si no puede, a través de su handler
        // podemos realizar ciertas acciones.

        // Indicar que tryEmitValue() nos devuelve, si queremos, el resultado del valor emitido (OK, FAIL_OVERFLOW, ...).
    }
}
