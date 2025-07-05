package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import com.jmunoz.sec01.subscriber.SubscriberImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

// Vamos a ver el comportamiento por defecto de `Flux.create()`, que puede no gustarnos, ya que no es lo que esperamos.
// El producer produce los items por adelantado, sin esperar a que el subscriber haga un request.
public class Lec04FluxCreateDownstreamDemand {

    private static final Logger log = LoggerFactory.getLogger(Lec04FluxCreateDownstreamDemand.class);

    public static void main(String[] args) {

        // Utilizo esta implementación de Subscriber para poder cancelar la petición.
        var subscriber = new SubscriberImpl();

        Flux.<String>create(fluxSink -> {
            for (int i = 0; i < 10; i++) {
                var name = Util.faker().name().firstName();
                log.info("generated: {}", name);
                fluxSink.next(name);
            }
            fluxSink.complete();
        }).subscribe(subscriber);

        // Nuestra implementación de SubscriberImpl por defecto no hace ningún request.
        // Lo que vemos en consola, en el log, es que se generan los 10 nombres, pero el subscriber no los recibe.
        // Es decir, el producer produce los items por adelantado.
        // En concreto, al ejecutar next() almacena los items en una cola, y el subscriber los puede obtener cuando quiera.
        // ¿Cuántos elementos caben en esta cola? El valor máximo es Integer.MAX_VALUE, que son 2^31-1 elementos, prácticamente infinito,
        // antes nos quedaremos sin memoria.
        // Si el producer produce más rápido de lo que el subscriber puede consumir, se acumulan los elementos en la cola.
        // A esto se le llama backpressure, y es un problema que debemos resolver. Lo veremos en otra sección.
        //
        // Hemos hablado de que debemos intentar ser lazy, y es cierto, pero a veces, hacer el trabajo por adelantado
        // puede ser útil, por ejemplo, podemos preparar la comida por la mañana y comerla a lo largo del día.
        // To-do depende del caso de uso.

        // Aquí recibe los datos.
        Util.sleepSeconds(2);
        subscriber.getSubscription().request(2);

        Util.sleepSeconds(2);
        subscriber.getSubscription().request(2);

        Util.sleepSeconds(2);
        subscriber.getSubscription().cancel();

        // Una vez cancelado, ya funciona como esperamos, ya que el subscriber no puede recibir más datos.
        subscriber.getSubscription().request(2);
    }
}
