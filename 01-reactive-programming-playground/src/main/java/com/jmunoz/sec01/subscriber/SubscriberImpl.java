package com.jmunoz.sec01.subscriber;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// No olvidar que Subscriber es la interface y nosotros creamos nuestra propia implementación
// para ver como funciona to-do esto.
// Esta implementación no es necesaria, ya que más adelante en el curso usaremos Project Reactor.
//
// El tipo de data que recibimos del Publisher es String, por eso Subscriber<String>
public class SubscriberImpl implements Subscriber<String> {

    private static final Logger log = LoggerFactory.getLogger(SubscriberImpl.class);
    private Subscription subscription;

    // Creamos este getter, para exponer subscription, ya que vamos a crear una clase demo.
    public Subscription getSubscription() {
        return subscription;
    }

    // Cuando nos subscribimos a un Publisher, este nos da el objeto Subscription.
    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
    }

    // Este es el méto-do que usará el Publisher para darnos la información.
    @Override
    public void onNext(String email) {
        log.info("received: {}", email);
    }

    // Si el Publisher tiene algún problema para darnos la información, llamará a este méto-do.
    @Override
    public void onError(Throwable throwable) {
        log.error("error", throwable);
    }

    // Cuando el Publisher termina de darnos la información y no hay más elementos que pueda dar.
    @Override
    public void onComplete() {
        log.info("completed!");
    }
}
