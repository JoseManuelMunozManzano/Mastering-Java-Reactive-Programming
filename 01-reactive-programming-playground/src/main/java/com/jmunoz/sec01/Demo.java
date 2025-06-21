package com.jmunoz.sec01;

import com.jmunoz.sec01.publisher.PublisherImpl;
import com.jmunoz.sec01.subscriber.SubscriberImpl;

import java.time.Duration;

/*
    1. publisher does not produce data unless subscriber requests for it.
    2. publisher will produce only <= subscriber requested items. publisher can also produce 0 items!
    3. subscriber can cancel the subscription, producer should stop at that moment as subscriber is no longer
        interested in consuming the data.
    4. producer can send the error signal to indicate something is wrong.
*/
public class Demo {

    public static void main(String[] args) throws InterruptedException {
        // Ir descomentando para probar el caso que se quiera.
        //
        // demo1();
        // demo2();
        // demo3();
        demo4();
    }

    // Probamos 1. publisher does not produce data unless subscriber requests for it.
    private static void demo1() {
        var publisher = new PublisherImpl();
        var subscriber = new SubscriberImpl();
        publisher.subscribe(subscriber);
    }

    // Probamos 2. publisher will produce only <= subscriber requested items. publisher can also produce 0 items!
    // Para el caso de 0 items, cambiar en la clase SubscriptionImpl el valor de MAX_ITEMS a 0.
    private static void demo2() throws InterruptedException {
        var publisher = new PublisherImpl();
        var subscriber = new SubscriberImpl();
        publisher.subscribe(subscriber);

        // Simulamos un procesamiento muy lento.
        // Cada petición de 3 items viene seguido de una espera de 2 segundos.
        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));

        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));

        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));

        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));

        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));
    }

    // Probamos 3. subscriber can cancel the subscription, producer should stop at that moment as subscriber
    //  is no longer interested in consuming the data.
    private static void demo3() throws InterruptedException {
        var publisher = new PublisherImpl();
        var subscriber = new SubscriberImpl();
        publisher.subscribe(subscriber);

        // Simulamos un procesamiento muy lento.
        // Cada petición de 3 items viene seguido de una espera de 2 segundos.
        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));

        subscriber.getSubscription().cancel();

        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));
    }

    // Probamos 4. producer can send the error signal to indicate something is wrong.
    private static void demo4() throws InterruptedException {
        var publisher = new PublisherImpl();
        var subscriber = new SubscriberImpl();
        publisher.subscribe(subscriber);

        // Simulamos un procesamiento muy lento.
        // Cada petición de 3 items viene seguido de una espera de 2 segundos.
        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));

        // Esta petición hace que el producer llame al onError() del subscriber.
        subscriber.getSubscription().request(11);
        Thread.sleep(Duration.ofSeconds(2));

        // Una vez llamado a onError() el publisher ya no produce nada.
        subscriber.getSubscription().request(3);
        Thread.sleep(Duration.ofSeconds(2));
    }
}