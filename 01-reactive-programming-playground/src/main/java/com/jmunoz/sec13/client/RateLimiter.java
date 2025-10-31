package com.jmunoz.sec13.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Como es una demo, se usa mucho static, pero en la vida real serían singleton Spring Bean.
public class RateLimiter {

    // Este map es mutable.
    // Por cada categoría, cuántos intentos hemos hecho.
    private static final Map<String, Integer> categoryAttempts = Collections.synchronizedMap(new HashMap<>());

    // Recordar que al inicializar, se ejecuta este static automáticamente.
    static {
        refresh();
    }

    static <T> Mono<T> limitCalls() {
        // Obtenemos del context la key category (la puede añadir UserService)
        // Si la tiene vemos si se permite la llamada.
        // Si no la tiene, directamente no permitimos la llamada.
        return Mono.deferContextual(ctx -> {
            var allowCall = ctx.<String>getOrEmpty("category")
                    .map(RateLimiter::canAllow)
                    .orElse(false);

            // Como estamos usando deferContextual, tenemos que devolver un publisher.
            // Mono.empty() significa que to-do está bien.
            // Mono.error() obviamente indica que no haremos la llamada.
            return allowCall ? Mono.empty() : Mono.error(new RuntimeException("exceeded the given limit!"));
        });
    }

    // Indicando synchronized, este méto-do permite acceso atómico, es decir, es Thread Safe.
    // Las llamadas concurrentes no interferirán unas con otras cuando se chequee y actualice el contador attempts.
    private static synchronized boolean canAllow(String category) {
        // Imaginemos standard.
        // Empezamos con 2 intentos y vamos restando.
        var attempts = categoryAttempts.getOrDefault(category, 0);
        if (attempts > 0) {
            categoryAttempts.put(category, attempts - 1);
            return true;
        }
        return false;
    }

    // Cada 5 segundos refrescamos el número de intentos a su máximo permitido.
    private static void refresh() {
        // No nos interesa el valor que se emite cada 5sg.
        // Lo que nos interesa es disparar, cada 5sg, el refresco del valor de nuestros intentos a su valor máximo.
        // Tener en cuenta que, sin el startWith(), el primer valor se emite tras 5sg de ejecutarse la app.
        // Necesitamos startWith() para que se emita un valor nada más ejecutarse la app.
        Flux.interval(Duration.ofSeconds(5))
                .startWith(0L)
                .subscribe(i -> {
                    categoryAttempts.put("standard", 2);
                    categoryAttempts.put("prime", 3);
                });
    }
}
