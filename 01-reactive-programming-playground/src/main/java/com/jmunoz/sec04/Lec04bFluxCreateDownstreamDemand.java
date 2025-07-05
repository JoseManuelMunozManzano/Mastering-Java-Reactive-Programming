package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import com.jmunoz.sec01.subscriber.SubscriberImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

// Vamos a cambiar el comportamiento por defecto de `Flux.create()`, para que se emitan los items solo
// cuando el subscriber haga un request, es decir bajo demanda.
public class Lec04bFluxCreateDownstreamDemand {

    private static final Logger log = LoggerFactory.getLogger(Lec04bFluxCreateDownstreamDemand.class);

    public static void main(String[] args) {
        // Comportamiento por defecto, el producer produce los items por adelantado.
//        produceEarly();

        // Comportamiento modificado, el producer produce los items solo cuando el subscriber hace un request.
        produceOnDemand();
    }

    // Solo se emiten los items cuando el subscriber hace un request.
    private static void produceOnDemand() {
        var subscriber = new SubscriberImpl();

        Flux.<String>create(fluxSink -> {
            // Este méto-do se llama cuando el subscriber hace un request.
            // Notar que tenemos en cuenta si se ha cancelado la suscripción.
            // En onComplete() lo podemos tener en cuenta con un contador, si por ejemplo, queremos emitir un máximo
            // de 100 elementos. En caso contrario, no hace falta.
            fluxSink.onRequest(request -> {
                for (int i = 0; i < request && !fluxSink.isCancelled(); i++) {
                    var name = Util.faker().name().firstName();
                    log.info("generated: {}", name);
                    fluxSink.next(name);
                }
            });
        }).subscribe(subscriber);

        // En este caso no veremos en el log que se generan los 10 nombres, ya que el producer no produce por adelantado,
        Util.sleepSeconds(2);
        subscriber.getSubscription().request(2);

        Util.sleepSeconds(2);
        subscriber.getSubscription().request(2);

        Util.sleepSeconds(2);
        subscriber.getSubscription().cancel();
        // Una vez cancelado, ya no se pueden emitir más datos, por lo que no veremos nada en el log.
        subscriber.getSubscription().request(2);
    }

    // De nuevo, aquí indicamos el comportamiento por defecto.
    private static void produceEarly() {
        var subscriber = new SubscriberImpl();

        Flux.<String>create(fluxSink -> {
            for (int i = 0; i < 10; i++) {
                var name = Util.faker().name().firstName();
                log.info("generated: {}", name);
                fluxSink.next(name);
            }
            fluxSink.complete();
        }).subscribe(subscriber);

        Util.sleepSeconds(2);
        subscriber.getSubscription().request(2);

        Util.sleepSeconds(2);
        subscriber.getSubscription().request(2);

        Util.sleepSeconds(2);
        subscriber.getSubscription().cancel();

        subscriber.getSubscription().request(2);
    }
}
