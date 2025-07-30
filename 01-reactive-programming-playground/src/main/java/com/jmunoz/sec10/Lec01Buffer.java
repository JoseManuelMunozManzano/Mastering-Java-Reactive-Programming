package com.jmunoz.sec10;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.time.Duration;

// Coleccionar items basados en un internal / size dado.
public class Lec01Buffer {

    public static void main(String[] args) {

//        demo1();
//        demo2();
//        demo3();
        demo4();

        // Para evitar que el programa termine inmediatamente al terminar el hilo principal.
        Util.sleepSeconds(60);
    }

    // buffer() por defecto espera al onComplete() del publisher o a
    // coleccionar Integer.MAX_VALUE elementos para emitirlos.
    private static void demo1() {
        eventStream()
                .buffer()
                .subscribe(Util.subscriber());
    }

    // También podemos indicar un parámetro de tamaño para el buffer.
    // En este caso, el buffer se emitirá cuando se alcance el tamaño especificado.
    // Para el último buffer, si no se alcanza el tamaño especificado, lo emitirá igualmente.
    //
    // Este enfoque tiene un problema: Si ya ha llegado el 10 item, pero no llega el 11 ni el 12, ¿qué pasa?
    // No recibiremos el item 10 porque está esperando al item 11.
    // Esto lo resolvemos en demo4()
    private static void demo2() {
        eventStream()
                .buffer(3)
                .subscribe(Util.subscriber());
    }

    // También podemos indicar un parámetro de tiempo para el buffer.
    // En este caso, el buffer se emitirá cada vez que transcurra el tiempo especificado.
    // Si el tiempo especificado es menor que el tiempo que tarda en emitirse un elemento,
    // no emite nada.
    private static void demo3() {
        eventStream()
                .buffer(Duration.ofMillis(500))
                .subscribe(Util.subscriber());
    }

    // Para evitar el problema de demo2() se puede usar un operador llamado bufferTimeout().
    // Este operador emite el buffer cuando se alcanza el tamaño especificado o cuando transcurre
    // el tiempo especificado, lo que ocurra primero.
    // Este operador acepta un número de elementos y un tiempo de espera.
    private static void demo4() {
        eventStream()
                .bufferTimeout(3, Duration.ofSeconds(1))
                .subscribe(Util.subscriber());
    }

    private static Flux<String> eventStream() {
        return Flux.interval(Duration.ofMillis(200))
                // Para demo1
                // Si no indicamos esto, no veremos ningún resultado porque buffer espera al
                // onComplete() o a coleccionar Integer.MAX_VALUE elementos.
                //
                // Para demo2 no haría falta porque ya emitimos los elementos en grupos de 3.
                //
                // Para demo3 no haría falta porque ya emitimos los elementos en cada 500 ms.
                //
                // Pero lo dejamos siempre porque queremos emitir solo 10 eventos.
                .take(10)
                // Para el problema indicado en demo2() indicamos Flux.never(), que se usa en escenarios de testing.
                // No envía ningún item ni el signal onComplete().
                // Comentar esta sentencia si se está probando demo1() o demo3().
                .concatWith(Flux.never())
                .map(i -> "event-" + (i + 1));
    }
}
