package com.jmunoz.sec04.assignment;

import reactor.core.publisher.Flux;

import java.nio.file.Path;

/*
    - Hacer el trabajo solo cuando se subscribe alguien.
    - Hacer el trabajo basado en la demanda.
    - Parar de producir cuando el subscriber cancela.
    - Producir solo los items pedidos.
    - El fichero debe cerrarse solo una vez.
*/
public interface FileReaderService {

    Flux<String> read(Path path);
}
