package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import com.jmunoz.sec04.helper.NameGenerator;
import reactor.core.publisher.Flux;

// Llevamos la lógica de la lambda a una clase aparte para que sea más fácil de reutilizar y mantener.
public class Lec02FluxCreateRefactor {

    public static void main(String[] args) {
        var generator = new NameGenerator();
        var flux = Flux.create(generator);
        flux.subscribe(Util.subscriber());

        for (int i = 0; i < 10; i++) {
            generator.generate();
        }
    }
}
