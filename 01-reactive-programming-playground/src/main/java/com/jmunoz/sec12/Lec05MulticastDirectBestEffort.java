package com.jmunoz.sec12;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Sinks;

import java.time.Duration;

public class Lec05MulticastDirectBestEffort {

    private static final Logger log = LoggerFactory.getLogger(Lec05MulticastDirectBestEffort.class);

    public static void main(String[] args) {
//        demo1();
        demo2();

        Util.sleepSeconds(10);
    }

    private static void demo1() {

        // Para entender el problema, ajustamos el tamaño de la cola.
        System.setProperty("reactor.bufferSize.small", "16");

        // A partir del cual emitiremos los items.
        // onBackPressureBuffer - bounded queue (la cola como máx. es de 256, y puede tener valores 1, 8, 16...)
        //
        // 1 - Soluciona el problema parcialmente.
//        var sink = Sinks.many().multicast().onBackpressureBuffer(100);
        var sink = Sinks.many().multicast().onBackpressureBuffer();

        // A partir del cual los subscribers recibirán los items.
        var flux = sink.asFlux();

        // El subscriber sam es muy rápido y el subscriber mike es muy lento.
        // Esto en la vida real es lo más normal, que distintos subscribers procesarán a distintas velocidades.
        flux.subscribe(Util.subscriber("sam"));
        flux.delayElements(Duration.ofMillis(200)).subscribe(Util.subscriber("mike"));

        for (int i = 1; i <= 100; i++) {
            Sinks.EmitResult result = sink.tryEmitNext(i);
            log.info("item: {}, result: {}", i, result);
        }

        // El problema es que, cuando tenemos dos subscribers y uno es más lento, afecta al rendimiento del otro
        // subscriber, y tampoco podemos entregar de forma segura los mensajes al otro subscriber.
        // Los mensajes que podemos entregar son los que entrar en el buffer (16)
        // Por tanto, el problema no puede resolverse completamente.
        //
        // ¿Qué podemos hacer en estos casos?
        // 1 - Incrementar el tamaño del buffer (onBackpressureBuffer) resuelve el problema parcialmente.
        //      Pero, ¿por qué el rendimiento de mike afecta al rendimiento de sam? sam debería obtener los mensajes
        //      rápidamente, antes que mike, pero no es así.
        //      Incrementar el tamaño del buffer resuelve el problema de entregar todos los items,
        //      pero no ha resuelto el problema del rendimiento.
    }

    // 2 - Para solucionar el problema del rendimiento, Reactor provee una opción que, en vez de usar
    //     onBackpressureBuffer, usa directBestEffort(), para centrarse en el subscriber rápido, ignorando
    //     el subscriber lento.
    private static void demo2() {

        // Para entender el problema, ajustamos el tamaño de la cola.
        System.setProperty("reactor.bufferSize.small", "16");

        // A partir del cual emitiremos los items.
        // Usando directBestEffort() nos centramos en el subscriber más rápido.
        var sink = Sinks.many().multicast().directBestEffort();

        // A partir del cual los subscribers recibirán los items.
        var flux = sink.asFlux();

        // El subscriber sam es muy rápido y el subscriber mike es muy lento.
        // Esto en la vida real es lo más normal, que distintos subscribers procesarán a distintas velocidades.
        flux.subscribe(Util.subscriber("sam"));
        flux.delayElements(Duration.ofMillis(200)).subscribe(Util.subscriber("mike"));

        // Solución 3: Notifico al producer que soy lento, y para ello le digo que ponga todos los mensajes
        // en un buffer y que ya los voy cogiendo.
        // Descomentar esta línea y comentar la anterior de mike para probar.
//        flux.onBackpressureBuffer().delayElements(Duration.ofMillis(200)).subscribe(Util.subscriber("mike"));

        for (int i = 1; i <= 100; i++) {
            Sinks.EmitResult result = sink.tryEmitNext(i);
            log.info("item: {}, result: {}", i, result);
        }

        // Con esta segunda solución, sam recibe los elementos muy rápido, pero mike solo recibe 1 item.
        // Pero esa era la intención de esta solución.
        // Como se ha dicho antes, no existe la solución perfecta, o entregamos todos los items
        // perdiendo rendimiento, o vamos a por el rendimiento a costa de que no todos los subscribers
        // reciban todos los elementos.
        //
        // Hay una tercera solución, que consiste en que esta solución, con directBestEffort,
        // es de tipo SyncBackpressure. Entonces, el subscriber más lento puede notificar al producer que es lento,
        // para que los elementos vayan a un buffer y el subscriber lento los procesa conforme vaya pudiendo.
        // De esta forma, sam va rápido y mike sigue recibiendo todos los items.
    }
}
