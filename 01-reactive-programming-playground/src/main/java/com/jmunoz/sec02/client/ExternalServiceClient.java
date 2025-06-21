package com.jmunoz.sec02.client;

import com.jmunoz.common.AbstractHttpClient;
import reactor.core.publisher.Mono;

public class ExternalServiceClient extends AbstractHttpClient {

    // Mandaremos el productId y queremos que nos devuelva el nombre del producto.
    //
    // Cuando se invoque este méto-do, creamos el Mono, que es capaz de enviar un request.
    // Pero la petición HTTP solo se envía cuando un subscriber se subscribe.
    //
    // Indicar que responseContent() devuelve un ByteBufFlux, que es un flujo de bytes.
    // Cuando usemos Spring WebFlux en el siguiente curso, la serialización/deserialización
    // será a un objeto, no a algo de tan bajo nivel como Reactor Netty.
    // Luego se convierte ese flujo a cadena de texto usando asString().
    // Y, por último, solo cogemos el primer valor, porque sabemos que hay uno o ninguno. Con esto convertimos de Flux a Mono.
    public Mono<String> getProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo01/product/" + productId)
                .responseContent()
                .asString()
                .next();
    }
}
