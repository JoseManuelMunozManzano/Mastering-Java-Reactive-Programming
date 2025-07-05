package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

// Vemos que Flux.generate() es sin estado.
// Cuando queremos mutar un estado dentro de synchronousSink, podemos encontrarnos con problemas.
// Aquí lo solucionamos.
public class Lec08bGenerateWithState {

    public static void main(String[] args) {
        // PETICIÓN: Paramos cuando encontremos el país Canada, o cuando se emitan como mucho 10 países.
        //
        // Se podría hacer con un operador, pero como producer, deberíamos poder parar la emisión cuando queramos.
        // No podemos usar un contador, porque cada vez que se invoca SynchronousSink, este se reinicia.
        //
        // SOLUCIÓN: Usamos los parámetros de la función generate para mantener el estado.
        Flux.generate(
                () -> 0, // Estado inicial
                (counter, sink) -> {
                    var country = Util.faker().country().name();
                    sink.next(country);
                    counter++;

                    if (counter == 10 || country.equalsIgnoreCase("Canada")) {
                        sink.complete(); // Paramos la emisión
                    }

                    return counter; // Devolvemos el nuevo estado
                }
        ).subscribe(Util.subscriber());
    }
}
