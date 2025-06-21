package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Lec09FluxInterval {

    public static void main(String[] args) {

        // Flux Interval existe para emitir elementos cada cierto intervalo de tiempo de una manera no bloqueante.
        Flux.interval(Duration.ofMillis(500))
                .map(i -> Util.faker().name().firstName())
                .subscribe(Util.subscriber());

        // Como es no bloqueante, el thread main terminará inmediatamente,
        // y por eso lo bloqueamos.
        // Notar que aunque el programa termine, el publisher sigue emitiendo elementos.
        // Lo suyo es cancelar el publisher cuando ya no nos interesa.
        Util.sleepSeconds(5);
    }
}
