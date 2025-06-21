package com.jmunoz.common;

import com.github.javafaker.Faker;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Mono;

import java.time.Duration;

// Creamos una instancia de nuestro subscriber.
public class Util {

    private static final Faker faker = Faker.instance();

    // Si solo necesitamos un subscriber, el nombre no es necesario.
    public static <T> Subscriber<T> subscriber() {
        return new DefaultSubscriberImpl<>("");
    }

    public static <T> Subscriber<T> subscriber(String name) {
        return new DefaultSubscriberImpl<>(name);
    }

    public static Faker faker() {
        return faker;
    }

    public static void sleepSeconds(int seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Demo - no importante realmente.
    // Lo relevante es la creación de la instancia.
    public static void main(String[] args) {
        var mono = Mono.just(1);

//        mono.subscribe(subscriber());
        mono.subscribe(subscriber("sub1"));
        mono.subscribe(subscriber("sub2"));
    }
}
