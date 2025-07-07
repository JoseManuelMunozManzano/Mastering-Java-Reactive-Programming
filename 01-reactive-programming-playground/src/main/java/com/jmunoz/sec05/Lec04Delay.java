package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.time.Duration;

// Vemos como retrasar la emisión de elementos en un flujo usando delayElements()
// El problema que tiene este delay es el mismo que tiene interval(), y es que se hace en un hilo diferente al hilo que emite los elementos.
public class Lec04Delay {
    public static void main(String[] args) {

        // Vemos que, usando delayElements(), el request se hace de uno en uno.
        // Es decir, cada segundo, se le comunica al producer que es hora de emitir un elemento.
        // Es decir, el producer no hace el trabajo por adelantado.
        Flux.range(1, 10)
                .log()
                .delayElements(Duration.ofSeconds(1))
                .subscribe(Util.subscriber());

        // Para que no termine el programa antes de que se emitan todos los elementos.
        Util.sleepSeconds(12);
    }
}
