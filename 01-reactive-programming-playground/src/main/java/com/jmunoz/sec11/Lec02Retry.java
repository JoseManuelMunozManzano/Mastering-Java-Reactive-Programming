package com.jmunoz.sec11;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

// El operador retry() se resubscribe cuando el producer emite la señal onError()
// Es preferible usar retryWhen() por la flexibilidad que aporta.
public class Lec02Retry {

    private static final Logger log = LoggerFactory.getLogger(Lec02Retry.class);

    public static void main(String[] args) {
//        demo1();

//        demo2();

        demo3();

        // Para demo2() y demo3() bloqueamos el hilo principal por algunos segundos.
        Util.sleepSeconds(10);
    }

    // Usando el operador retry() volvemos a requerir data del publisher cuando este nos emite la señal onError().
    // Podemos indicar el número de veces que queremos reintentar. Si no lo indicamos, reintenta de forma indefinida,
    // pero lo normal es indicar un número de reintentos.
    // Si tras ese número de reintentos el publisher sigue mandando la señal onError(), entonces se lanza la excepción
    // RetryExhaustedException y también obtendremos la excepción original.
    private static void demo1() {
        getCountryName()
                .retry(2)
                .subscribe(Util.subscriber());
    }

    // Podemos indicar un operador retryWhen() que espera un objeto de tipo Retry.
    //
    // Indicando Retry.indefinitely() estaríamos reintentando una y otra vez (sería como usar retry()).
    // O podemos indicar Retry.max(2) para reintentar, en este caso, un máximo de 2 veces (sería como usar retry(2)).
    // O podemos indicar Retry.fixedDelay() para reintentar la petición pasado un tiempo. Se indica también el número
    //      máximo de veces que se quiere reintentar.
    //
    // Podemos indicar un callback. En el ejemplo uso doBeforeRetry()
    // Esto es interesante porque usando retry() o retryWhen() el error no se ve, porque se está reintentando, pero
    // con el callback, podemos ver el error, número de errores...
    private static void demo2() {
        getCountryName()
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .doBeforeRetry(rs -> log.info("retrying", rs.failure())))
                .subscribe(Util.subscriber());
    }

    // Puede que no queramos reintentar ante algún tipo de error, por ejemplo, ante errores 400 Bad Request no querremos
    // volver a reintentar la petición.
    // Podemos usar un operador filter() que acepta un Predicate de Throwable.
    //
    // Como se ha dicho, si sigue fallando tras los reintentos, obtenemos dos excepciones,
    // RetryExhaustedException y la original. Si solo queremos la original, podemos indicando onRetryExhaustedThrow()
    // que acepta una BiFunction (spec, signal) donde signal es la excepción original, y devuelve un Throwable
    // al subscriber.
    private static void demo3() {
        getCountryName()
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        // Aquí estamos diciendo que, si la clase de excepción es igual a RuntimeException, reintentamos.
                        .filter(ex -> RuntimeException.class.equals(ex.getClass()))
                        // Aquí estamos diciendo que, si la clase de excepción es igual a IllegalArgumentException, reintentamos.
                        // .filter(ex -> IllegalArgumentException.class.equals(ex.getClass())))
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .subscribe(Util.subscriber());
    }

    private static Mono<String> getCountryName() {
        // Emitimos señal onError
        var atomicInteger = new AtomicInteger(0);

        // non-blocking IO
        return Mono.fromSupplier(() -> {
                    // La segunda vez que nos resubscribimos ya funciona.
                    // Cambiar a 5 para que siga fallando en los ejemplos, y ver que excepciones devuelve.
                    if (atomicInteger.incrementAndGet() < 5) {
                        throw new RuntimeException("oops!");
                    }

                    return Util.faker().country().name();
                })
                .doOnError(err -> log.info("ERROR: {}", err.getMessage()))
                .doOnSubscribe(s -> log.info("subscribing"));
    }
}
