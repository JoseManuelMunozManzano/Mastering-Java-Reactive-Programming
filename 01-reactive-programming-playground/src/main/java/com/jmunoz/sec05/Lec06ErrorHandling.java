package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
    Como manejar errores en un pipeline reactivo.

    En programación tradicional, tenemos el bloque try-catch para manejar excepciones.

    ¿Qué pasa en programación reactiva?

    - onErrorReturn
        - Si queremos devolver un valor hardcode o una computación simple.
        - Podemos manejar el error dependiendo del tipo de excepción, por ejemplo, devolver -1 solo en caso de IllegalArgumentException o la excepción en caso contrario.
        - Se puede añadir más de uno, con distintos tipos de excepciones y un valor de respaldo para cualquier otro tipo de error.
        - Se recibe ese valor y el programa termina.

    - onErrorResume
        - No queremos devolver un valor hardcode.
        - Digamos que llamamos a un Product Service que devuelve un error response. En caso de error, queremos llamar a otro fallback service para obtener la respuesta.
        - Se puede añadir más de uno, con distintos tipos de excepciones y un fallback service para cada uno.

    - onErrorComplete
        - En este caso lo que queremos es que si ocurre un error, se ejecute onComplete, pero que no devuelva la excepción.
        - Se deja de emitir valores y se completa el flujo sin errores.

    - onErrorContinue
        - En este caso, si ocurre un error, lo omitimos como si no hubiera ocurrido y continuamos con el flujo.
        - Evitarlo to-do lo posible, ya que puede llevar a comportamientos inesperados y difíciles de depurar.
        - A pesar de su utilidad, onErrorContinue() presenta inconvenientes.
            - Requiere soporte explícito de cada operador, ya que quienes siguen estrictamente la especificación podrían ignorarla por completo sin dejar de cumplirla.
            - Se recomienda utilizar onErrorResume() en cada publisher individual cuando sea necesario.

    Todos estos onErrorxxx (se pueden mezclar) son buenos ponerlos justo antes de subscribe, para que si se produce un error, en cualquier parte del pipeline, se pueda manejar.
    Indicar que este tratamiento de errores funciona tanto en Flux como en Mono.
*/
public class Lec06ErrorHandling {

    private static final Logger log = LoggerFactory.getLogger(Lec06ErrorHandling.class);

    public static void main(String[] args) {
        // onErrorReturn();

        // onErrorResume();

        // onErrorComplete();

        // onErrorContinue();
    }

    // En caso de error, emitir complete.
    private static void onErrorComplete() {
        Mono.error(new RuntimeException("oops"))
                .onErrorComplete() // Si ocurre un error, se ejecuta onComplete
                .subscribe(Util.subscriber());
    }

    // En caso de error, hacer como que no ha pasado
    private static void onErrorContinue() {
        Flux.range(1, 10)
                .map(i -> i == 5 ? 5/0 : i) // Simulamos un error al dividir por cero
                .onErrorContinue((ex, obj) -> log.error("==> {}", obj, ex)) // Si ocurre un error, lo omitimos y continuamos con el flujo
                .subscribe(Util.subscriber());
    }

    // Cuando queremos devolver un valor hardcode o una computación simple en caso de error.
    private static void onErrorReturn() {
        Flux.range(1, 10)
                .map(i -> i == 5 ? 5/0 : i) // Simulamos un error al dividir por cero
                .onErrorReturn(IllegalArgumentException.class, -1)
                .onErrorReturn(ArithmeticException.class, -2)
                .onErrorReturn(-3) // Valor de respaldo para cualquier otro tipo de error
                .subscribe(Util.subscriber());
    }

    // Cuando queremos usar otro publisher en caso de error, como un fallback service.
    private static void onErrorResume() {
        Mono.error(new RuntimeException("oops"))
                .onErrorResume(ArithmeticException.class, ex -> fallback1())
                .onErrorResume(ex -> fallback2())  // Si no es ArithmeticException, llamamos a otro fallback
                .onErrorReturn(-5)               // Si falla fallback2, o cualquier otro error que ocurra, devolvemos -5
                .subscribe(Util.subscriber());
    }

    // Imaginemos que este méto-do es un microservicio.
    private static Mono<Integer> fallback1() {
        return Mono.fromSupplier(() -> Util.faker().random().nextInt(10, 100));
    }

    // Imaginemos que este méto-do es otro microservicio.
    private static Mono<Integer> fallback2() {
        return Mono.fromSupplier(() -> Util.faker().random().nextInt(100, 1000));

        // Si queremos que falle este fallback, podemos lanzar una excepción.
//        return Mono.error(new IllegalArgumentException());
    }
}
