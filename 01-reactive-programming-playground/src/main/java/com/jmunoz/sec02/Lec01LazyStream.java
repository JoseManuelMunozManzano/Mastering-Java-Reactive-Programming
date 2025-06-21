package com.jmunoz.sec02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

// El objetivo de esta clase es entender que un flujo (stream) por defecto es lazy.
// Hasta que el flujo no se conecta a una salida no se ejecuta ni se obtiene el resultado.
//
// La programación reactiva es igual.
// Hasta que no nos conectemos a un subscriber no se ejecuta nada ni obtendremos ningún resultado.
public class Lec01LazyStream {

    private static final Logger log = LoggerFactory.getLogger(Lec01LazyStream.class);

    public static void main(String[] args) {

        // Java 8 introdujo la clase Stream, que por defecto es lazy.
        // A peek() le pasamos el consumer.
        // Pero Stream es lazy, por lo que no emitirá nada hasta que lo saquemos del flujo, a lista, a array...
        Stream.of(1)
                .peek(i -> log.info("received: {}", i))
                .toList();
    }
}
