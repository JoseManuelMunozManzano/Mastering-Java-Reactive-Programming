package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

public class Lec07FluxGenerateUntil {

    public static void main(String[] args) {
        // Solo tomamos los valores hasta que el país sea Canadá.

        // Forma uno de hacerlo.
        // Parando desde el provider.
//        demo1();

        // Forma dos de hacerlo.
        // Parando usando un operador.
        demo2();
    }

    private static void demo1() {
        Flux.generate(synchronousSink -> {
                    String country = Util.faker().country().name();
                    synchronousSink.next(country);

                    if (country.equalsIgnoreCase("canada")) {
                        synchronousSink.complete();
                    }
                })
                .subscribe(Util.subscriber());
    }

    private static void demo2() {
        Flux.<String>generate(synchronousSink -> {
                    String country = Util.faker().country().name();
                    synchronousSink.next(country);
                })
                .takeUntil(c -> c.equalsIgnoreCase("canada"))
                .subscribe(Util.subscriber());
    }
}
