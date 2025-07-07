package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;

/*
    timeout - producirá un error de timeout.
        - Podemos manejarlo como parte de los métodos onError
    También existe un méto-do sobrecargado que acepta un publisher
    Podemos tener varios timeouts, el más cercano al subscriber tomará efecto para el subscriber.
*/
public class Lec09Timeout {

    private static final Logger log = LoggerFactory.getLogger(Lec09Timeout.class);

    public static void main(String[] args) {

        // No vamos a esperar más de 1 segundo para que se complete el Mono.
        // Devuelve un error si no se completa en ese tiempo.
        //
        // Recogemos el error y lo manejamos (porque queremos, podemos dejarlo fallar), en este caso, con un operador `onErrorReturn`.
        //
        // O, el mismo operador `timeout` puede recibir un segundo Mono que se ejecutará si el primero falla por timeout.
        getProductName()
                .timeout(Duration.ofSeconds(1), fallback())
//                .onErrorReturn("fallback")
                .subscribe(Util.subscriber());

        // Bloqueamos el hilo principal para que no termine antes de que se complete el Mono.
        Util.sleepSeconds(5);

        multipleTimeouts();
    }

    // Representa un servicio externo que puede tardar en responder.
    private static Mono<String> getProductName() {
        return Mono.fromSupplier(() -> "service - " + Util.faker().commerce().productName())
                .delayElement(Duration.ofSeconds(3)); // Simula un retraso de 3 segundos
    }

    // Fallback service.
    // Representa un microservicio, o también un acceso a BD.
    private static Mono<String> fallback() {
        return Mono.fromSupplier(() -> "fallback - " + Util.faker().commerce().productName())
                .delayElement(Duration.ofMillis(300))
                .doFirst(() -> log.info("do first"));
    }

    private static void multipleTimeouts() {
        // Esto podría ser un méto-do reutilizable de otra librería que no deberíamos modificar.
        var mono = getProductName()
                .timeout(Duration.ofSeconds(1), fallback());

        // Si es necesario, podemos aplicar otro timeout aquí.
        // Y este es el que se va a aplicar, el más cercano al subscriber.
        // Pero tener en cuenta que si el tiempo del primer timeout es menor que el segundo, puede dar error el
        // primer timeout (o ejecutar el fallback) y el segundo no va a esperar ese tiempo.
        // Es decir, en este timeout se puede reducir el tiempo, pero no ampliarlo.
        mono
                .timeout(Duration.ofMillis(5000))
                .subscribe(Util.subscriber());

        Util.sleepSeconds(5);
    }
}
