package com.jmunoz.sec06;

import com.jmunoz.common.Util;
import com.jmunoz.sec04.helper.NameGenerator;
import reactor.core.publisher.Flux;

// Vamos a corregir el problema por el cual Flux.create no permite varios subscribers.
public class Lec05FluxCreateIssueFix {

    public static void main(String[] args) {
        var generator = new NameGenerator();

        // Indicando `share()` corregimos el problema de que Flux.create no permite varios subscribers.
        // Se convierte en un hot publisher.
        // Ahora, muchos subscribers se conectan a una sola fuente de datos.
        // Probar a eliminar `share()` y ver que solo el segundo subscriber recibe datos. Esto es por la clase generator, méto-do accept()
        var flux = Flux.create(generator).share();

        flux.subscribe(Util.subscriber("sub1"));
        flux.subscribe(Util.subscriber("sub2"));

        for (int i = 0; i < 10; i++) {
            generator.generate();
        }
    }
}
