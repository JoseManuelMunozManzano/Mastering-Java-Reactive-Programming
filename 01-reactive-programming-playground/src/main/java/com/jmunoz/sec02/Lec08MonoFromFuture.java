package com.jmunoz.sec02;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

// Usado para convertir un CompletableFuture en un publisher Mono.
public class Lec08MonoFromFuture {

    private static final Logger log = LoggerFactory.getLogger(Lec08MonoFromFuture.class);

    public static void main(String[] args) {

        // Aquí estamos en el thread main.
        // Para que el subscriber obtenga el valor (descomentar para la prueba), tenemos que bloquear el thread main,
        // ya que cuando este thread termina, termina el programa y no ha dado tiempo a que el subscriber obtenga
        // el valor.
        //
        // Este enfoque tiene un problema. Si comentamos la parte del subscriber y solo generamos el publisher
        // veremos que llama a getName(), es decir, un Completable Future NO ES LAZY.
        Mono.fromFuture(getName());
//                .subscribe(Util.subscriber());

        // Para que sea lazy, la forma de ejecutar el CompletableFuture es la siguiente, en la que
        // usamos un Supplier que si es Lazy.
        Mono.fromFuture(Lec08MonoFromFuture::getName);

        // Asi bloqueamos el thread main.
        Util.sleepSeconds(1);
    }

    // Un CompletableFuture usa un thread pool separado (un onPool-worker, ver log)
    private static CompletableFuture<String> getName() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Generating name");
            return Util.faker().name().firstName();
        });
    }
}
