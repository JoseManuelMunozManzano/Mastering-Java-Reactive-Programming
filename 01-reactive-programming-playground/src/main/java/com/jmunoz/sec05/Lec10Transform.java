package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Lec10Transform {

    private static final Logger log = LoggerFactory.getLogger(Lec10Transform.class);

    record Customer(int id, String name) {}
    record PurchaseOrder(String productName, int price, int quantity) {}

    public static void main(String[] args) {
        // Sin usar el méto-do transform.
        // Vemos que hay 3 pasos del pipeline que se repiten en los dos pipelines.
        getCustomers()
                .doOnNext(i -> log.info("received: {}", i))
                .doOnComplete(() -> log.info("completed!"))
                .doOnError(err -> log.error("error", err))
                .subscribe();

        getPurchaseOrders()
                .doOnNext(i -> log.info("received: {}", i))
                .doOnComplete(() -> log.info("completed!"))
                .doOnError(err -> log.error("error", err))
                .subscribe();

        // Esta variable podría venir de una propiedad de configuración.
        var isDebugEnabled = true;

        // Usando el méto-do transform para evitar repetir los pasos del pipeline.
        // Usamos isDebugEnabled para decidir si añadimos el operador de depuración o no.
        // Function.identity() es una función que devuelve el mismo objeto que recibe como parámetro. No hace ningún cambio.
        // Es decir, podemos añadir y eliminar operadores en tiempo de ejecución, basado en flags.
        getCustomers()
                .transform(isDebugEnabled ? addDebugger() : Function.identity())
                .subscribe();

        getPurchaseOrders()
                .transform(addDebugger())
                .subscribe();
    }

    private static Flux<Customer> getCustomers() {
        return Flux.range(1, 3)
                .map(i -> new Customer(i, Util.faker().name().firstName()));
    }

    private static Flux<PurchaseOrder> getPurchaseOrders() {
        return Flux.range(1, 5)
                .map(i -> new PurchaseOrder(Util.faker().commerce().productName(), i, i * 10));
    }

    // Obtenemos un flux, le añadimos los operadores que queramos y devolvemos el flux resultante.
    // Es una forma de crear un operador personalizado.
    private static <T> UnaryOperator<Flux<T>> addDebugger() {
        return flux -> flux
                .doOnNext(i -> log.info("received: {}", i))
                .doOnComplete(() -> log.info("completed!"))
                .doOnError(err -> log.error("error", err));
    }
}
