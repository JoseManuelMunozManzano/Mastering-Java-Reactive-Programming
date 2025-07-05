package com.jmunoz.sec04;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.util.stream.IntStream;

public class Lec05TakeOperator {

    public static void main(String[] args) {
//        take();

//        takeWhile();

        takeUntil();
    }

    private static void take() {
        // Ejemplo con Java Streams (para comparar con Flux).
        // Imaginemos que usamos un flujo de enteros, rango cerrado de 1 a 10.
        // Esto emitirá los números del 1 al 10.
        // Pero si indicamos limit(3), solo tomará los primeros 3 números.
        IntStream.rangeClosed(1, 10)
                .limit(3)
                .forEach(System.out::println);

        // Para Flux es lo mismo.
        // Take es un operator, también llamado processor, que actúa como un subscriber para Flux.range() y a la vez
        // como un producer para el subscriber.
        // Si se indican más elementos en take() de los que hay en el flujo, simplemente se completará el flujo, en
        // este caso, se completará después de emitir los 10 elementos.
        Flux.range(1, 10)
                .log("take")
                .take(3) // Toma los primeros 3 elementos del flujo
                .log("sub")
                .subscribe(Util.subscriber());
    }

    private static void takeWhile() {
        // takeWhile() nos permite emitir items mientras se cumpla una condición.
        // Para cuando la condición no se cumple, y el flujo se cancela y se completa.
        // No incluye el item que no cumple la condición.
        Flux.range(1, 10)
                .log("take")
                .takeWhile(i -> i < 5)
                .log("sub")
                .subscribe(Util.subscriber());
    }

    private static void takeUntil() {
        // takeUntil() nos permite emitir items hasta que se cumpla una condición.
        // Para cuando la condición se cumple, y el flujo se cancela y se completa.
        // Incluye el item que cumple la condición.
        Flux.range(1, 10)
                .log("take")
                .takeUntil(i -> i < 5)
                .log("sub")
                .subscribe(Util.subscriber());
    }
}
