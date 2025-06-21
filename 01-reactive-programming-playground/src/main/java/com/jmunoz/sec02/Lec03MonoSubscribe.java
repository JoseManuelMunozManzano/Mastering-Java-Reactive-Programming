package com.jmunoz.sec02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

// Vamos a ver las sobrecargas de subscribe()
public class Lec03MonoSubscribe {

    private static final Logger log = LoggerFactory.getLogger(Lec03MonoSubscribe.class);

    public static void main(String[] args) {
        // El publisher.
        // Con map además hacemos una transformación.
        // En el ejemplo dividimos por cero para probar el onError del méto-do subscribe.
        // Descomentar para probar.
        var mono = Mono.just(1);
//                .map(i -> i / 0);

        // Nos subscribimos usando un consumer (prog. funcional)
        // Obligatoriamente, hemos usado un consumer para tratar onNext().
        // Optativamente, hemos usado otro consumer para tratar onError().
        // Optativamente, hemos usado un Runnable para tratar onComplete() (no hay opción de Consumer)
        //
        // Notar que no hemos hecho ninguna request. La petición va implícita en esta implementación.
        // Es decir, cuando el subscriber obtiene el objeto subscription, automáticamente se hace la request.
        //
        // Pero también es posible indicar un último parámetro optativo con la request.
        // En el primer ejemplo comentado, el subscriber cancela la subscripción, por lo que no se emitiría nada.
        // En el segundo ejemplo comentado, el subscriber realiza la petición.
        mono.subscribe(
                i -> log.info("received: {}", i),
                err -> log.error("error", err),
                () -> log.info("completed")
                // , subscription -> subscription.request(1)
                // subscription -> subscription.cancel()
        );

    }
}
