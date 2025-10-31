package com.jmunoz.sec08;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/*
    Reactor provee estrategias para manejar el backpressure.
    - buffer - onBackpressureBuffer()
    - drop  - onBackpressureDrop()
    - latest - onBackpressureLatest()
    - error - onBackpressureError()
*/
public class Lec05BackPressureStrategies {

    private static final Logger log = LoggerFactory.getLogger(Lec05BackPressureStrategies.class);

    public static void main(String[] args) {

        var producer = Flux.create(sink -> {
                            // Se usa !sink.isCancelled() para evitar que se sigan generando datos si el subscriber se ha cancelado.
                            for (int i = 1; i < 500 && !sink.isCancelled(); i++) {
                                log.info("generating {}", i);
                                sink.next(i);
                                // Simula un retraso en la generación de datos, 20 items por segundo.
                                Util.sleep(Duration.ofMillis(50));
                            }
                            sink.complete();
                        }
                // Parámetro `OverflowStrategy` en `Flux.create()` para definir la estrategia de manejo de backpressure.
                // Es útil si queremos usar una estrategia para todos los subscribers.
//                , FluxSink.OverflowStrategy.DROP
                )
                .cast(Integer.class)
                .subscribeOn(Schedulers.parallel());

        // El subscriber consume 1 item por segundo.
        // Notar que no se usa Util.subscriber(). subscriber() es suficiente para pedir to-dos los items.
        //
        // buffer: Usamos onBackpressureBuffer() para almacenar los items que no se pueden procesar inmediatamente.
        //         El subscriber consume 1 item por segundo desde el buffer, pero sin parar.
        // error: Usamos onBackpressureError() para indicar que si el subscriber no puede procesar los items, se lanzará una excepción.
        //        Como existe un limitRate(1), cuando el publisher emite más de 1 item sin que lo haya consumido el subscriber, se lanzará una excepción.
        // fixed sized buffer: Usamos onBackpressureBuffer(10) para limitar el tamaño del buffer a 10 items. Más allá de eso, se lanzará una excepción.
        //        Recordar que eso significa que si el subscriber no consume items y la cola llega a contener más de 10 items, se lanzará una excepción.
        // drop: Usamos onBackpressureDrop() para descartar los items que no se pueden procesar inmediatamente.
        //        Ejemplo: El subscriber va haciendo peticiones de 1 elemento (limitRate), pero el publisher emite muchos más.
        //        El operador pasa el elemento al subscriber y descarta los restantes.
        // latest: Usamos onBackpressureLatest() para mantener el último item emitido por el publisher.
        //        Ejemplo: El subscriber va haciendo peticiones de 1 elemento (limitRate), pero el publisher emite muchos más.
        //        El operador pasa el elemento al subscriber y va descartando los restantes salvo el último, que será el que
        //        se pase al subscriber ante la nueva petición.
        producer
//                .onBackpressureBuffer()
//                .onBackpressureError()
//                .onBackpressureBuffer(10)   // 2 - 11 items en el buffer. Falla al 12 porque el buffer ya está lleno.
//                .onBackpressureDrop()   // En el ejemplo lo he trabajado junto al operador log().
                .onBackpressureLatest()
                .log()
                .limitRate(1)
                .publishOn(Schedulers.boundedElastic())
                .map(Lec05BackPressureStrategies::timeConsumingTask)
                .subscribe();

        // Bloquea el hilo principal para que no termine antes de que se complete el flujo.
        Util.sleepSeconds(60);
    }

    // Operator muy lento
    private static int timeConsumingTask(int i) {
        // Como no usamos Util.subscriber(), indicamos aquí el log para poder ver el item recibido.
        log.info("received {}", i);
        Util.sleepSeconds(1);
        return i;
    }
}
