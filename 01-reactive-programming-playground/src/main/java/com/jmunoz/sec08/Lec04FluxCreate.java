package com.jmunoz.sec08;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class Lec04FluxCreate {

    private static final Logger log = LoggerFactory.getLogger(Lec04FluxCreate.class);

    // En las clases pasadas hemos usado Flex.generate(). Usando este méto-do, Reactor controla el bucle e invoca
    // el méto-do de generación de datos basado en la capacidad de procesamiento del subscriber.
    // Es decir, gestiona automáticamente el backpressure.
    //
    // Pero en este ejemplo usamos Flex.create(), que no controla el bucle de generación de datos. Lo controlamos nosotros.
    // Es decir, no gestiona backpressure automáticamente. Tenemos que manejarlo nosotros.
    // Se emiten datos sin parar.
    public static void main(String[] args) {

        System.setProperty("reactor.bufferSize.small", "16");

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
                )
                .cast(Integer.class)
                .subscribeOn(Schedulers.parallel());

        // El subscriber consume 1 item por segundo.
        // Notar que no se usa Util.subscriber(). subscriber() es suficiente para pedir to-dos los items.
        //
        // El comportamiento es el siguiente: El consumer recibe los 16 items del buffer interno de Reactor, uno cada segundo.
        // Después para de recibir, pero el producer sigue generando datos hasta 500.
        // Cuando termina el producer, el consumer sigue recibiendo los items restantes, empezando en el 17, uno cada segundo.
        //
        // Esto ocurre porque el producer se da cuenta de que el consumer es muy lento, y comienza a gestionar una cola interna separada,
        // sin pasar los datos al subscriber.
        // En programas reales, esto puede causar un gran problema como OutOfMemoryError.
        //
        // Si usamos limitRate(1) empeora el problema porque nuestra cola es solo de 1 item. Es decir, el consumer recibe un item,
        // y ya (al menos antes recibía 16 items), tiene que esperar a que termine el producer para recibir los siguientes items.
        producer
//                .limitRate(1)
                .publishOn(Schedulers.boundedElastic())
                .map(Lec04FluxCreate::timeConsumingTask)
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
