package com.jmunoz.sec02;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public class Lec05MonoFromSupplier {

    private static final Logger log = LoggerFactory.getLogger(Lec05MonoFromSupplier.class);

    public static void main(String[] args) {
        var list = List.of(1, 2, 3);

        // Tenemos que dar el resultado de la suma a nuestro subscriber.
        // Este enfoque tiene un problema.
        // La parte Mono.just(sum(list)) YA HACE LA EJECUCIÓN aunque no devuelve el valor
        // mientras no haya un subscriber (en el ejemplo lo hay, pero lo he comentado)
        // NO TENEMOS QUE REALIZAR LA EJECUCIÓN hasta que un subscriber se subscribe al publisher.
        //
        // Solo debemos usar just cuando ya tenemos el valor en memoria, no cuando tenemos que calcularlo.
        Mono.just(sum(list));
//                .subscribe(Util.subscriber());

        // Cuando tenemos que calcular valores que un subscriber nos va a pedir, para ser más eficientes,
        // usaremos fromSupplier. Esto asegura que se demora la ejecución (lazy) hasta que un subscriber
        // nos pide la data.
        Mono.fromSupplier(() -> sum(list));
//                .subscribe(Util.subscriber());
    }

    // Imaginemos que esta operación es muy costosa.
    private static int sum(List<Integer> list) {
        log.info("Finding the sum of {}", list);

        return list.stream().mapToInt(a -> a).sum();
    }
}
