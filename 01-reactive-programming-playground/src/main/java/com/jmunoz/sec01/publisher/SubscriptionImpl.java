package com.jmunoz.sec01.publisher;

import com.github.javafaker.Faker;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// No olvidar que Subscription es la interface y nosotros creamos nuestra propia implementación
// para ver como funciona to-do esto.
// Esta implementación no es necesaria, ya que más adelante en el curso usaremos Project Reactor.
//
// Usando esta Subscription, el Subscriber puede hacer los requests.
// Creamos esta clase en el package publisher porque es este quien da la subscripción.
public class SubscriptionImpl implements Subscription {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionImpl.class);
    // Estamos simulando que la BD tiene 10 items, por tanto, este será el máximo de elementos que pueda producir el publisher.
    private static final int MAX_ITEMS = 10;
    private final Faker faker;
    private final Subscriber<? super String> subscriber;
    private boolean isCancelled;
    private int count = 0;

    // Esta firma, Subscriber<? super String> subscriber, la cojo del méto-do subscribe de PublisherImpl.
    public SubscriptionImpl(Subscriber<? super String> subscriber) {
        this.subscriber = subscriber;
        this.faker = Faker.instance();
    }

    // El subscriber llama a esta función cuando quiere que el publisher produzca información.
    @Override
    public void request(long requested) {
        if (isCancelled) {
            return;
        }

        log.info("subscriber has requested {} items", requested);

        // Simulamos un error para demo4, para que el publisher llame al onError.
        if (requested > MAX_ITEMS) {
            this.subscriber.onError(new RuntimeException("validation failed"));
            this.isCancelled = true;
            return;
        }

        // Como mucho el publisher puede producir 10 items.
        // Usamos la dependencia faker para producir data aleatoria, en este caso direcciones de email.
        for (int i = 0; i < requested && count < MAX_ITEMS; i++) {
            count++;
            this.subscriber.onNext(this.faker.internet().emailAddress());
        }

        if (count == MAX_ITEMS) {
            log.info("no more data to produce");
            this.subscriber.onComplete();
            this.isCancelled = true;
        }
    }

    // El subscriber llama a esta función cuando quiere que el publisher pare de producir información.
    @Override
    public void cancel() {
        log.info("subscriber has cancelled");
        this.isCancelled = true;
    }
}
