package com.jmunoz.sec08;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
    Reactor maneja el backpressure de forma automática.

    Podemos ajustar el límite.
*/
public class Lec02LimitRate {

    private static final Logger log = LoggerFactory.getLogger(Lec02LimitRate.class);

    public static void main(String[] args) {

        var producer = Flux.generate(
                        () -> 1,
                        (state, sink) -> {
                            log.info("generating: {}", state);
                            sink.next(state);
                            return ++state;
                        }
                )
                .cast(Integer.class)
                .subscribeOn(Schedulers.parallel());

        // Hay una forma de poder decirle al producer que el consumer es lento, y que, aunque se pidan todos los elementos,
        // la velocidad de procesamiento es lenta.
        // Se usa el méto-do limitRate() para limitar la tasa de emisión de elementos, en este caso a 5 elementos por segundo.
        // De nuevo, cuando se consume un 75% de los elementos, el publisher genera más elementos, hasta que en la cola
        // vuelva a haber 5 items sin consumir.
        producer
                .limitRate(5)
                .publishOn(Schedulers.boundedElastic())
                .map(Lec02LimitRate::timeConsumingTask)
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
