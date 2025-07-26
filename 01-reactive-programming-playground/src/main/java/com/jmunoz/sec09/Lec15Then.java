package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/*
    "then" puede ser útil cuando no estamos interesados en el resultado de un publisher /
    necesitamos ejecución secuencial de tareas asíncronas.
*/
public class Lec15Then {

    private static final Logger log = LoggerFactory.getLogger(Lec15Then.class);

    public static void main(String[] args) {

        var records = List.of("a", "b", "c");

        // No estamos interesados en saber como han ido los resultados intermedios, solo si ha terminado bien o no.
        // Flux<String> se convierte en Mono<Void>, es decir, no devuelve lo que se envía como parte de onNext(),
        // sino que espera a la señal onComplete() del publisher y esa si la pasa al subscriber.
        // En caso de onError(), también lo pasa al subscriber.
        // Para este ejemplo, el resultado será: received complete!
        saveRecords(records)
                .then()
                .subscribe(Util.subscriber());

        // A veces, este operador también es útil para ejecutar varios producers en un orden específico.
        // Por ejemplo, después de haber guardado los registros, necesitamos enviar una notificación informando
        // de que se han guardado de manera exitosa (o no).
        //
        // Gracias al then() del ejemplo anterior, sabemos que estas sentencias de abajo no se ejecutarán
        // hasta que se complete el publisher saveRecords(records).
        // Sin el then(), el subscriber de sendNotification(records) se ejecutaría inmediatamente, a la vez que
        // el publisher saveRecords(records), y no esperaríamos a que se completara el primero
        sendNotification(records)
                .subscribe(Util.subscriber());

        // Como then() acepta otro publisher, esto también es válido:
        saveRecords(records)
                .then(sendNotification(records))
                .subscribe(Util.subscriber());

        // Bloqueamos el hilo principal para que no termine antes que nuestro ejemplo.
        Util.sleepSeconds(5);
    }

    // Esto sería un controlador que inserta registros en una base de datos.
    private static Flux<String> saveRecords(List<String> records) {
        return Flux.fromIterable(records)
                .map(r -> "saved " + r)
                .delayElements(Duration.ofMillis(500));
    }

    private static Mono<Void> sendNotification(List<String> records) {
        return Mono.fromRunnable(() -> log.info("all these {} records saved successfully!", records));
    }
}
