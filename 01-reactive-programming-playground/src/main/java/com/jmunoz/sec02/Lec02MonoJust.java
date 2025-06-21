package com.jmunoz.sec02;

import com.jmunoz.sec01.subscriber.SubscriberImpl;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class Lec02MonoJust {

    public static void main(String[] args) {

        // El factory method just() es la forma más fácil de crear un publisher.
        // Se emitirá el valor cuando un subscriber se subscriba a este publisher.
        // Mono acepta cualquier tipo.
        Mono<String> monoString = Mono.just("José");

        // Indicar Mono<String> o Publisher<String> es básicamente lo mismo.
        Publisher<String> publisher = Mono.just("José");

        // Para simplificar, usaré var
        // Para subscribirnos hay muchísimos métodos que podemos usar (métodos subscribe()) y permiten
        // programación funcional.
        // Indicar que no hace falta usar nuestra implementación SubscriberImpl(). Lo hacemos para
        // jugar con ella, pero podemos pasar un Consumer, por ejemplo.
        // Ver los distintos métodos subscribe() para ver las posibilidades existentes.
        // Por último, hacemos la petición.
        //
        // Por tanto, para obtener el valor:
        // 1. Creamos el publisher.
        // 2. Nos subscribimos.
        // 3. Hacemos la petición.
        var mono = Mono.just("José");
        var subscriber = new SubscriberImpl();
        mono.subscribe(subscriber);
        subscriber.getSubscription().request(10);

        // Una vez ejecutado onComplete, ya no podremos volver a obtener data ni cancelar.
        // Estas dos llamadas no tienen ningún efecto.
        subscriber.getSubscription().request(10);
        subscriber.getSubscription().cancel();
    }
}
