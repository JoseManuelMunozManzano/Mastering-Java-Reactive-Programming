package com.jmunoz.sec03.client;

import com.jmunoz.common.AbstractHttpClient;
import reactor.core.publisher.Flux;

public class ExternalServiceClient extends AbstractHttpClient {

    // Aunque indicamos un Flux<String>, puede ser cualquier tipo de objeto.
    // Eso sí, probablemente haya que añadir algún mecanismo de deserialización.
    // Esto también lo veremos en el curso de Spring WebFlux.
    public Flux<String> getNames() {
        return this.httpClient.get()
                .uri("/demo02/name/stream")
                .responseContent()
                .asString();
    }

    public Flux<Integer> getPriceChanges() {
        return this.httpClient.get()
                .uri("/demo02/stock/stream")
                .responseContent()
                .asString()
                .map(Integer::parseInt);
    }
}
