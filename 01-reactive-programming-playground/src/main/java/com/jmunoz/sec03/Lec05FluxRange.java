package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

public class Lec05FluxRange {

    public static void main(String[] args) {
        // Podemos usar Flux.range para generar un rango de números y sería como una especie de bucle for
        // en programación reactiva.

        // El rango comienza en 1 y quiero 10 números, por tanto, mostrará los números del 1 al 10.
        Flux.range(1, 10)
                .subscribe(Util.subscriber());

        // El rango comienza en 3 y quiero 10 números, por tanto, mostrará los números del 3 al 12.
        Flux.range(3, 10)
                .subscribe(Util.subscriber());

        // Generando 10 nombres aleatorios usando la librería Faker.
        Flux.range(1, 10)
                .map(i -> Util.faker().name().firstName())
                .subscribe(Util.subscriber());
    }
}
