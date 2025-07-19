package com.jmunoz.sec08;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
    Reactor maneja el backpressure de forma automática.

    System.setProperty("reactor.bufferSize.small", "16");
    El valor mínimo es 16.
*/
public class Lec01BackPressureHandling {

    private static final Logger log = LoggerFactory.getLogger(Lec01BackPressureHandling.class);

    public static void main(String[] args) {

        // Reactor maneja colas internas para manejar el backpressure. Si vamos a la clase Queues
        // veremos el tamaño de esos buffers, y veremos el valor 256 por defecto (se puede incrementar).
        // De ahí sacamos esta sentencia, donde le decimos a Reactor que el tamaño mínimo de los buffers es 16 (el mínimo que permite Reactor actualmente).
        // (En el mandato se ha cambiado getProperty por setProperty)
        //
        // Indicar que en programas reales no es necesario hacer esto, es mejor manejar un límite, como se ve en Lec02LimitRate
        //
        // Comentar esta línea para ver el comportamiento por defecto de Reactor, que es generar 256 elementos en vez de 16.
        System.setProperty("reactor.bufferSize.small", "16");

        // Emite por siempre, pero si ejecutamos vemos que para en el item 16.
        // Esto es porque el méto-do generate() genera 16 elementos de data y espera a que el subscriber los consuma
        // para seguir generando más.
        // En realidad al consumir un 75% de los elementos, el publisher genera más elementos, hasta que en la cola vuelva a haber 16 items sin consumir.
        // Esta es una forma de manejar automáticamente el backpressure en Reactor.
        var producer = Flux.generate(
                        () -> 1,
                        (state, sink) -> {
                            log.info("generating: {}", state);
                            sink.next(state);
                            return ++state;
                        }
                )
                .cast(Integer.class)
                // Descomentar para usar otro scheduler thread distinto al main thread.
                // Vemos el mismo comportamiento de generar 16 elementos y luego para hasta que el subscriber los consume
                // (de nuevo, cuando se consume un 75% de los elementos, el publisher genera más elementos).
                .subscribeOn(Schedulers.parallel());

        // El publisher se ejecuta en el hilo principal.
        // Para crear el problema de backpressure, tenemos que usar scheduler threads.
        // Si to-do se ejecuta en el hilo principal, no hay problema de backpressure.
        producer
                .publishOn(Schedulers.boundedElastic())
                .map(Lec01BackPressureHandling::timeConsumingTask)
                .subscribe(Util.subscriber());

        // Bloquea el hilo principal para que no termine antes de que se complete el flujo.
        Util.sleepSeconds(60);
    }

    // Operator muy lento
    private static int timeConsumingTask(int i) {
        Util.sleepSeconds(1);
        return i;
    }
}
