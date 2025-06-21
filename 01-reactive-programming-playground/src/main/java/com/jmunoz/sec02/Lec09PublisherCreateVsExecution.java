package com.jmunoz.sec02;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

// Crear un objeto publisher es una operación ligera.
// La ejecución de lógica de negocio pesada debe retrasarse.
public class Lec09PublisherCreateVsExecution {

    private static final Logger log = LoggerFactory.getLogger(Lec09PublisherCreateVsExecution.class);

    public static void main(String[] args) {
        // Nada se imprime porque no se hace nada, gracias a fromSupplier que es Lazy.
        // SOLO CREAMOS EL PUBLISHER
        getName();

        // Si nos subscribimos entonces si se devuelve el resultado.
        // CREAMOS EL PUBLISHER Y LO EJECUTAMOS
        getName()
                .subscribe(Util.subscriber());
    }

    private static Mono<String> getName() {
        // Esto se ejecuta siempre.
        log.info("enter the method");

        // Este publisher se crea siempre, pero solo se ejecuta si hay subscriber.
        // Es distinto crear este publisher a ejecutarlo!
        return Mono.fromSupplier(() -> {
            log.info("Generating name");
            Util.sleepSeconds(3);
            return Util.faker().name().firstName();
        });
    }
}
