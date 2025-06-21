package com.jmunoz.sec02;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public class Lec06MonoFromCallable {

    private static final Logger log = LoggerFactory.getLogger(Lec06MonoFromCallable.class);

    public static void main(String[] args) {
        var list = List.of(1, 2, 3);

        // Usaremos fromCallable en vez de fromSupplier cuando la operación que se envuelve lanza checked exceptions.
        Mono.fromCallable(() -> sum(list))
                .subscribe(Util.subscriber());

        // Supplier no las soporta y da error en tiempo de desarrollo y nos obliga a usar try ... catch para manejarlas.
        Mono.fromSupplier(() -> {
            try {
                return sum(list);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).subscribe(Util.subscriber());
    }

    // Imaginemos que esta operación es muy costosa y lanza una checked exception.
    private static int sum(List<Integer> list) throws Exception {
        log.info("Finding the sum of {}", list);

        return list.stream().mapToInt(a -> a).sum();
    }
}
