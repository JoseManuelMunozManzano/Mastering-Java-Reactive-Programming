package com.jmunoz.common;

import com.github.javafaker.Faker;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.UnaryOperator;

// Creamos una instancia de nuestro subscriber.
public class Util {

    private final static Logger log = LoggerFactory.getLogger(Util.class);
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

    // Para las pruebas de sec08/Lec04FluxCreate
    public static void sleep(Duration duration) {
        try {
            // En Java 17, para que funcione: Thread.sleep(duration.toMillis());
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Generalizamos el operador doOnSubscribe, doOnCancel y doOnComplete,
    // El nombre es solo para propósitos de depuración.
    public static <T> UnaryOperator<Flux<T>> fluxLogger(String name) {
        return flux -> flux
                .doOnSubscribe(s -> log.info("Subscribing to {}", name))
                .doOnCancel(() -> log.info("Cancelling {}", name))
                .doOnComplete(() -> log.info("{} completed", name));
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
