package com.jmunoz.sec12;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

import java.time.Duration;

public class Lec06MulticastDirectAllOrNothing {

    private static final Logger log = LoggerFactory.getLogger(Lec06MulticastDirectAllOrNothing.class);

    public static void main(String[] args) {
        demo1();

        Util.sleepSeconds(10);
    }

    private static void demo1() {

        // Para entender el problema, ajustamos el tamaño de la cola.
        System.setProperty("reactor.bufferSize.small", "16");

        // A partir del cual emitiremos los items.
        // Usando directAllOrNothing() queremos decir: O podemos dar el item a todos los subscribers o a ninguno.
        var sink = Sinks.many().multicast().directAllOrNothing();

        // A partir del cual los subscribers recibirán los items.
        var flux = sink.asFlux();

        // El subscriber sam es muy rápido y el subscriber mike es muy lento.
        // Esto en la vida real es lo más normal, que distintos subscribers procesarán a distintas velocidades.
        // Usando directAllOrNothing(), como el segundo subscriber es muy lento, solo vamos a poder dar el primer
        // item a los dos subscribers. A partir del segundo item, ya no se le da a ninguno.
        flux.subscribe(Util.subscriber("sam"));
        flux.delayElements(Duration.ofMillis(200)).subscribe(Util.subscriber("mike"));

        for (int i = 1; i <= 100; i++) {
            Sinks.EmitResult result = sink.tryEmitNext(i);
            log.info("item: {}, result: {}", i, result);
        }
    }
}
