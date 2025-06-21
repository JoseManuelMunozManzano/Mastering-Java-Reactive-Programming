package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

public class Lec02MultipleSubscribers {

    public static void main(String[] args) {

        var flux = Flux.just(1, 2, 3, 4, 5, 6);

        // Podemos tener múltiples suscriptores a un mismo flujo.
        flux.subscribe(Util.subscriber("sub1"));
        flux.subscribe(Util.subscriber("sub2"));

        // Este suscriptor va a tener un filtro por el que solo recibirá los números pares.
        // Además, se añade un map para transformar los datos antes de ser emitidos.
        flux
                .filter(i -> i % 2 == 0)
                .map(i -> i + "a")
                .subscribe(Util.subscriber("sub3"));

        // Este suscriptor tiene un filtro para obtener solo valores mayores que 7.
        // No recibirá ningún valor.
        flux
                .filter(i -> i > 7)
                .subscribe(Util.subscriber("sub4"));
    }
}
