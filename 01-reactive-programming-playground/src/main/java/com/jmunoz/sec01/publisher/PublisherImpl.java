package com.jmunoz.sec01.publisher;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

// No olvidar que Publisher es la interface y nosotros creamos nuestra propia implementación
// para ver como funciona to-do esto.
// Esta implementación no es necesaria, ya que más adelante en el curso usaremos Project Reactor.
//
// El Publisher devuelve un tipo String, de ahí Publisher<String>
public class PublisherImpl implements Publisher<String> {

    // El Publisher solo conecta la Subscription con el Subscriber.
    // La subscripción obtiene el subscriber.
    // El Subscriber se subscribe obteniendo la subscripción en este méto-do.
    @Override
    public void subscribe(Subscriber<? super String> subscriber) {
        var subscription = new SubscriptionImpl(subscriber);
        subscriber.onSubscribe(subscription);
    }
}
