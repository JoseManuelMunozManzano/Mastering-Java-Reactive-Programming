package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

/*
    Handle se comporta más o menos como filter + map

    1 => -2
    4 => no enviar
    7 => error
    cualquier otro => enviarlo
*/
public class Lec01Handle {

    public static void main(String[] args) {
        Flux<Integer> flux = Flux.range(1, 10);

        // Cada vez que se aplica un operator, se crea una nueva instancia.
        // Indicar que estos objetos son muy livianos y no hay problema en crear muchos.
        Flux<Integer> flux1 = flux.handle((item, sink) -> {
            sink.error(new RuntimeException("oops!"));
        });

        // Nos tenemos que subscribir al último operator (o al que queremos que se aplique).
        flux1.subscribe(Util.subscriber());

        // Este es el ejemplo final, con to-do agrupado en un solo pipeline.
        // Se indica el cast porque SynchronousSink no entiende el tipo y se convierte a un Flux<Object>.
        // No es obligatorio y lo haremos si tenemos muy claro el tipo de dato.
        Flux.range(1, 10)
                .handle((item, sink) -> {
                   switch (item) {
                       case 1 -> sink.next(-2);
                       case 4 -> { /* no enviar nada */ }
                       case 7 -> sink.error(new RuntimeException("oops!"));
                       default -> sink.next(item);
                   }
                })
                .cast(Integer.class)
                .subscribe(Util.subscriber());
    }
}
