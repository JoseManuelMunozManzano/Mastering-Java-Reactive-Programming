package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

/*
    do hooks / callbacks

    Ejecutar y entender lo que está pasando, sobre to-do el orden de ejecución que es un poco caótico.

    - Comentado el bucle que emite los items, y producer emite una señal de complete. take(2) está comentado. take(4) está comentado.

        El orden de ejecución comienza del subscriber para arriba hasta llegar al producer.
            Ahí se escribe doFirst-2 y luego doFirst-1.
        Luego, vuelve a bajar desde el producer hasta el subscriber. El producer da el objeto subscription.
            Ahí se escribe doOnSubscribe-1 y luego doOnSubscribe-2.
        Vuelve a llegar al subscriber, que hace la petición de items, y va de nuevo para arriba.
            Ahí se escribe doOnRequest-2 y luego doOnRequest-1.
        Llega al producer, que, en un primer ejemplo con el bucle comentado, emite una señal de complete.
            Ahí se escribe el log `producer begins` y doOnComplete-1, doOnTerminate-1, doOnComplete-2 y doOnTerminate-2.
        Cuando llega al subscriber, recibe la señal de completado y este escribe el log `subscriber received complete!`
        Vuelve a ir para arriba.
            Ahí se ejecuta doFinally-2 y doFinally-1.
        Llega de nuevo al producer que escribe el log `producer ends`.

    - Comentado el bucle que emite los items, y producer emite una señal de error. take(2) está comentado. take(4) está comentado.

        El orden de ejecución comienza del subscriber para arriba hasta llegar al producer.
            Ahí se escribe doFirst-2 y luego doFirst-1.
        Luego, vuelve a bajar desde el producer hasta el subscriber. El producer da el objeto subscription.
            Ahí se escribe doOnSubscribe-1 y luego doOnSubscribe-2.
        Vuelve a llegar al subscriber, que hace la petición de items, y va de nuevo para arriba.
            Ahí se escribe doOnRequest-2 y luego doOnRequest-1.
        Llega al producer, que, en un primer ejemplo con el bucle comentado, emite una señal de error.
            Ahí se escribe el log `producer begins` y doOnError-1, doOnTerminate-1, doOnError-2 y doOnTerminate-2.
        Cuando llega al subscriber, recibe la señal de error y este escribe el log `subscriber error` y aparece la excepción RuntimeException: oops.
        Vuelve a ir para arriba.
            Ahí se ejecuta doFinally-2 y doFinally-1.
        Llega de nuevo al producer que escribe el log `producer ends`.

    - Comentado el bucle que emite los items, y producer emite una señal de error. take(2) está sin comentar. take(4) está comentado.

        El orden de ejecución comienza del subscriber para arriba hasta llegar al producer.
            Ahí se escribe doFirst-2 y luego doFirst-1.
        Luego, vuelve a bajar desde el producer hasta el subscriber. El producer da el objeto subscription.
            Ahí se escribe doOnSubscribe-1 y luego doOnSubscribe-2.
        Vuelve a llegar al subscriber, que hace la petición de items, y va de nuevo para arriba.
            Ahí se escribe doOnRequest-2 y luego doOnRequest-1.
            Es en doOnRequest-1 donde se limita la petición a 2 items. Aquí es donde está el cambio respecto al ejemplo anterior.
        To-do lo demás, igual que el ejemplo anterior, pero ahora se limita a 2 items.

    - Sin comentar el bucle que emite los items, y producer emite una señal de complete. take(2) está sin comentar. take(4) está comentado.

        El orden de ejecución comienza del subscriber para arriba hasta llegar al producer.
            Ahí se escribe doFirst-2 y luego doFirst-1.
        Luego, vuelve a bajar desde el producer hasta el subscriber. El producer da el objeto subscription.
            Ahí se escribe doOnSubscribe-1 y luego doOnSubscribe-2.
        Vuelve a llegar al subscriber, que hace la petición de items, y va de nuevo para arriba.
            Ahí se escribe doOnRequest-2 y luego doOnRequest-1.
            Es en doOnRequest-1 donde se limita la petición a 2 items.
        Llega al producer, que, con el bucle descomentado, produce 4 items. Emite el valor 0.
            Ahí se escribe el log `producer begins` y doOnNext-1: 0, doOnNext-2: 0.
        Cuando llega al subscriber, recibe la emisión del item 0 y este escribe el log `subscriber received: 0`.
        No hace el sentido de ir para arriba, sino que el producer emite el valor 1.
            Ahí se escribe doOnNext-1: 1, doOnNext-2: 1.
        El subscriber recibe la emisión del item 1 y este escribe el log `subscriber received: 1`.
        Como take(2) está operativo, se cancela la subscripción. Va de take(2) hacia el producer. Notar que no es el subscriber el que cancela y por eso no se escribe doOnCancel-2.
            Ahí se escribe doOnCancel-1, doFinally-1: cancel.
        Ahora, take(2) va hacia el subscriber indicando que se ha completado la subscripción.
            Ahí se escribe doOnComplete-2 y doOnTerminate-2.
        Cuando llega al subscriber, recibe la señal de completado y este escribe el log `subscriber received complete!`
        Vuelve a ir para arriba.
            Ahí se ejecuta doFinally-2: Complete.
        Cuando llega al producer, como no le importa que take(2) haya cancelado la subscripción (se ha diseñado este ejemplo así intencionalmente), sigue emitiendo los items. Pero están descartados.
            Ahí se escribe doOnDiscard-1: 2, doOnDiscard-2: 2, doOnDiscard-1: 3, doOnDiscard-2: 3.
        Finalmente, el producer escribe el log `producer ends`.

    - Sin comentar el bucle que emite los items, y producer emite una señal de complete. take(2) está sin comentar. take(4) está sin comentar.

        El comportamiento es muy parecido, salvo que doOnRequest-2: 4 (en vez de Integer.maxValue) y doOnRequest-1: 2 (sigue valiendo 2, que es menor que 4, es decir, pasamos de 4 a 2)

    - Sin comentar el bucle que emite los items, y producer emite una señal de complete. take(2) está sin comentar y modificado a take(20). take(4) está sin comentar.

        Aquí vemos que doOnRequest-2: 4 (en vez de Integer.maxValue) y doOnRequest-1: 4 (y no 20, porque 4 es menor y si se para ahí, jamás se van a emitir 20 items).
*/
public class Lec03DoCallbacks {
    private static final Logger log = LoggerFactory.getLogger(Lec03DoCallbacks.class);

    public static void main(String[] args) {

        // De todos estos ejemplos, solo doOnNext mutan el valor.
        //
        // doOnComplete es un méto-do que se invoca cuando el producer manda la señal de complete (solo una vez).
        // doFirst es un méto-do que se invoca lo primero.
        // doOnNext se ejecuta cuando el valor se emite (por cada item)
        //       Muta el valor original
        // doOnSubscribe se invoca con un objeto Subscription. El publisher mandará el objeto subscription por el flujo.
        // doOnRequest se invoca cuando el subscriber hace una petición de items.
        // doOnError se invoca cuando el producer manda una señal de error.
        // doOnTerminate se invoca cuando el producer manda una señal de complete o error.
        //       Es como una combinación de doOnComplete y doOnError.
        // doOnCancel se invoca cuando el subscriber cancela la suscripción.
        // doOnDiscard se invoca cuando un item es producido, pero no es consumido por el subscriber, ya sea porque no lo recibe o porque se ha descartado o cancelado. Nos da el objeto descartado.
        // doFinally es el paso final. Se invoca cuando el producer manda una señal de complete o error, o cuando el subscriber cancela la suscripción.
        Flux.<Integer>create(fluxSink -> {
                    log.info("producer begins");

                    // Comentar este bucle para ver los efectos cuando solo se emite una señal de complete o error.
                    // Comentar/Descomentar complete y error para ver los efectos de cada uno.
                    for (int i = 0; i < 4; i++) {
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
//                    fluxSink.error(new RuntimeException("oops"));
                    log.info("producer ends");
                })
                .doOnComplete(() -> log.info("doOnComplete-1"))
                .doFirst(() -> log.info("doFirst-1"))
                .doOnNext(item -> log.info("doOnNext-1: {}", item))
                .doOnSubscribe(subscription -> log.info("doOnSubscribe-1: {}", subscription))
                .doOnRequest(request -> log.info("doOnRequest-1: {}", request))
                .doOnError(error -> log.info("doOnError-1: {}", error.getMessage()))
                .doOnTerminate(() -> log.info("doOnTerminate-1")) // complete or error case
                .doOnCancel(() -> log.info("doOnCancel-1"))
                .doOnDiscard(Object.class, o -> log.info("doOnDiscard-1: {}", o))
                .doFinally(signal -> log.info("doFinally-1: {}", signal)) // finally irrespective of the reason
                 .take(2)
                .doOnComplete(() -> log.info("doOnComplete-2"))
                .doFirst(() -> log.info("doFirst-2"))
                .doOnNext(item -> log.info("doOnNext-2: {}", item))
                .doOnSubscribe(subscription -> log.info("doOnSubscribe-2: {}", subscription))
                .doOnRequest(request -> log.info("doOnRequest-2: {}", request))
                .doOnError(error -> log.info("doOnError-2: {}", error.getMessage()))
                .doOnTerminate(() -> log.info("doOnTerminate-2")) // complete or error case
                .doOnCancel(() -> log.info("doOnCancel-2"))
                .doOnDiscard(Object.class, o -> log.info("doOnDiscard-2: {}", o))
                .doFinally(signal -> log.info("doFinally-2: {}", signal)) // finally irrespective of the reason
                .take(4)
                .subscribe(Util.subscriber("subscriber"));
    }
}
