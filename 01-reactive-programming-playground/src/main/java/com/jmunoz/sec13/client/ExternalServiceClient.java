package com.jmunoz.sec13.client;

import com.jmunoz.common.AbstractHttpClient;
import reactor.core.publisher.Mono;

public class ExternalServiceClient extends AbstractHttpClient {

    public Mono<String> getBook() {
        return this.httpClient.get()
                .uri("/demo07/book")
                .responseContent()
                .asString()
                // limitCall() es un publisher al que nos subscribimos,
                // y chequea si se permite la llamada http, usando la categoría.
                // Si va bien, limitCall() devuelve una señal empty, es decir,
                // no hay data que dar, por lo que seguirá para arriba (va de abajo, del subscriber hacia arriba)
                // y hará la llamada http.
                // Si limitCall emite un error, volverá al subscriber, es decir, no hace la llamada http.
                .startWith(RateLimiter.limitCalls())
                // En este punto sabremos cuál es la categoría del usuario. Es lo primero que se ejecuta.
                .contextWrite(UserService.userCategoryContext())
                .next();
    }
}
