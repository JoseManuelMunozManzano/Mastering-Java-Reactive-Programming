package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import com.jmunoz.sec09.client.ExternalServiceClient;
import reactor.core.publisher.Flux;

// En el ejercicio Lec08ZipAssignment.java se usaba un bucle para tratar los productos como Mono.
// Ahora vamos a usar flatMap() para trabajar con Flux.
public class Lec12FluxFlatMapAssignment {

    public static void main(String[] args) {
        var client = new ExternalServiceClient();

        // Solo creamos un publisher Flux, por lo que a diferencia de `Lec08ZipAssignment`
        // donde se creaban 10 Mono, solo obtenemos `received complete` solo una vez.
        //
        // Probar como ejemplo a indicar/eliminar el parámetro concurrency con valor 3.
        Flux.range(1, 10)
                        .flatMap(client::getProduct, 3)
                .subscribe(Util.subscriber());

        Util.sleepSeconds(6);
    }
}
