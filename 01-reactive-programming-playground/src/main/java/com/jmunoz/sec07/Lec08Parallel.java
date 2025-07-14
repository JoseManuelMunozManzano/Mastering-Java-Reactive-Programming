package com.jmunoz.sec07;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

// Lo normal es no tener que necesitar procesamiento paralelo.
// Es preferible un IO no bloqueante para llamadas a la red (ver Lec06EventLoopIssueFix).
// Solo en caso de tareas que hacen un uso muy intensivo de CPU, si queremos usar todas nuestras CPU, entonces es posible que tenga sentido un procesamiento paralelo.
// En una arquitectura de microservicios, donde tendremos muchas llamadas de red, es mejor IO no bloqueante.
public class Lec08Parallel {

    public static final Logger log = LoggerFactory.getLogger(Lec07PublishOnSubscribeOn.class);

    public static void main(String[] args) {

        // Esta ejecución es muy lenta porque se ejecuta en un solo hilo.
        //
        // Añadimos los operadores `parallel()` y `runOn()` para mejorar el rendimiento.
        // Indicar que el Scheduler indicado en `runOn()` no tiene por qué ser Schedulers.parallel().
        // También indicar que se puede indicar el número de hilos que se quieren usar. Por defecto usa el número de núcleos del procesador.
        //
        // Reactor nos da una opción para volver a ejecutar el código de forma secuencial, si se quiere, con el operador `sequential()`.
        //
        // Con esto, sabiendo cuál es la parte que consume más tiempo, podemos mejorar el rendimiento de la aplicación usando procesamiento paralelo,
        // y luego volver a secuencial si nos hace falta.
        Flux.range(1, 10)
                .parallel(3) // Indica que se quieren usar 3 hilos para el procesamiento paralelo. Tres tareas en paralelo.
                .runOn(Schedulers.parallel())
                .map(Lec08Parallel::process)
                .sequential()
                .map(i -> i + "a")
                .subscribe(Util.subscriber());

        Util.sleepSeconds(10);
    }

    // Simulamos tarea que consume mucho tiempo.
    private static int process(int i) {
        log.info("time consuming task: {}", i);
        Util.sleepSeconds(1);
        return i * 2;
    }
}
