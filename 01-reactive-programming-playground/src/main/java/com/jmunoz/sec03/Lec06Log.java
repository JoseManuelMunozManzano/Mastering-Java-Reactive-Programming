package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

public class Lec06Log {

    public static void main(String[] args) {

        // Usaremos el operador log() para imprimir información de depuración.
        // Esto es necesario porque entre el publisher y el subscriber lo normal es que haya muchos operadores
        // intermedios, de forma que un valor que se emite puede que nunca llegue al subscriber.
        // Para saber por qué ese valor no llega, es que usamos el operador log().
        //
        // El operador log() actúa como un subscriber para el producer, y como un producer para el siguiente operador.
        // Es lo que se conoce como Processor u Operator.
        //
        // Se puede indicar un nombre al operador log() para que se imprima en la salida de depuración.
        Flux.range(1, 5)
                .log("range-map")
                .map(i -> Util.faker().name().firstName())
                .log("map-subscriber")
                .subscribe(Util.subscriber());
    }
}
