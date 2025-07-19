package com.jmunoz.sec08;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Lec03MultipleSubscribers {

    private static final Logger log = LoggerFactory.getLogger(Lec03MultipleSubscribers.class);

    public static void main(String[] args) {

        // El publisher ajusta su velocidad de emisión de elementos según la velocidad de los subscribers.
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

        // El primer subscriber es lento.
        // Para este subscriber, el publisher generará elementos a una tasa de 5 elementos por segundo.
        producer
                .limitRate(5)
                .publishOn(Schedulers.boundedElastic())
                .map(Lec03MultipleSubscribers::timeConsumingTask)
                .subscribe(Util.subscriber("sub1"));

        // ¡El segundo subscriber es rápido!
        // Toma 100 elementos.
        // El publisher generará elementos a una tasa de 100 elementos por segundo.
        producer
                .take(100)
                .publishOn(Schedulers.boundedElastic())
                .subscribe(Util.subscriber("sub2"));

        // Bloquea el hilo principal para que no termine antes de que se complete el flujo.
        Util.sleepSeconds(60);
    }

    // Operator muy lento
    private static int timeConsumingTask(int i) {
        Util.sleepSeconds(1);
        return i;
    }
}
