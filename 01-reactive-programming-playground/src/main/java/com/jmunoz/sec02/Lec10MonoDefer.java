package com.jmunoz.sec02;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public class Lec10MonoDefer {

    private static final Logger log = LoggerFactory.getLogger(Lec10MonoDefer.class);

    public static void main(String[] args) {

        // Aunque no se subscriba nadie, la creación del publisher lleva mucho tiempo.
        createPublisher();
//                .subscribe(Util.subscriber());

        // Podemos retrasar la creación del publisher usando defer()
        // El publisher se creará (y ejecutará) cuando algún subscriber se subscriba.
        Mono.defer(Lec10MonoDefer::createPublisher);
//                .subscribe(Util.subscriber());
    }

    // Aunque lo normal es que la creación de un publisher sea muy rápida,
    // en este caso en concreto es una operación muy pesada.
    private static Mono<Integer> createPublisher() {
        log.info("Creating publisher");
        var list = List.of(1, 2, 3);
        Util.sleepSeconds(1);
        return Mono.fromSupplier(() -> sum(list));
    }

    // Lógica de negocio con operaciones pesadas.
    private static int sum(List<Integer> list) {
        log.info("Finding the sum of {}", list);
        Util.sleepSeconds(3);
        return list.stream().mapToInt(a -> a).sum();
    }
}
