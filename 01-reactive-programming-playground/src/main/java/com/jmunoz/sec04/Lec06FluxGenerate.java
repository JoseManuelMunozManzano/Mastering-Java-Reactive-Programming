package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/*
    Flux.generate
    - Invoca la expresión lambda una y otra vez, basado en la demanda del subscriber.
    - Solo puede emitir un valor por invocación.
    - Parará cuando se invoque complete()
    - Parará cuando se invoque error()
    - Parará si se cancela la suscripción.
*/
public class Lec06FluxGenerate {

    private static final Logger log = LoggerFactory.getLogger(Lec06FluxGenerate.class);

    public static void main(String[] args) {

        // Con SynchronousSink se puede emitir solo un valor.
        // Pero si no se añade complete(), no para de emitir ese valor de forma infinita basado en la demanda.
        Flux.generate(synchronousSink -> {
            log.info("invoked");
            synchronousSink.next(1);    // Este valor se emite correctamente.
//            synchronousSink.next(2);     // Error, no se puede llamar a next más de una vez.
//            synchronousSink.complete();  // Lo comentamos para poder invocar el SynchronousSink más de una vez.
//            synchronousSink.error(new RuntimeException("oops"));  // Lo comentamos para poder invocar el SynchronousSink más de una vez.
        })
                .take(4)                // Solo tomamos 4 valores (demanda 4). Si esto lo comentamos la demanda es infinita.
                .subscribe(Util.subscriber());
    }
}
