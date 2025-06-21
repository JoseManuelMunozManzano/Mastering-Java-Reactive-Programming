package com.jmunoz.sec02;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

// El caso de uso es el siguiente. Si necesitamos invocar un méto-do y después enviar empty,
// entonces usaremos fromRunnable.
// De nuevo, si no hay subscriber, como fromCallable y fromSubscriber son lazy, no se hace nada.
public class Lec07MonoFromRunnable {

    private static final Logger log = LoggerFactory.getLogger(Lec07MonoFromRunnable.class);

    public static void main(String[] args) {

        // Probar con productId 1 para obtener valor.
        getProductName(2)
                .subscribe(Util.subscriber());
    }

    private static Mono<String> getProductName(int productId) {
        if (productId == 1) {
            return Mono.fromSupplier(() -> Util.faker().commerce().productName());
        }

        // En este escenario, devolviendo empty, no sabemos los productos que no existen, y eso es un problema
        // para un negocio.
        //
//        return Mono.empty();

        // En vez de usar empty(), invocaremos el méto-do que indica a nuestro negocio los productos no disponibles
        // y luego devolveremos al cliente empty, ya que los productos no están disponibles.
        return Mono.fromRunnable(() -> notifyBusiness(productId));
    }

    private static void notifyBusiness(int productId) {
        log.info("notifying business on unavailable product {}", productId);
    }
}
