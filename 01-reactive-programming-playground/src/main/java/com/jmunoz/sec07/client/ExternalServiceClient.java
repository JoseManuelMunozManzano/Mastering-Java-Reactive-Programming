package com.jmunoz.sec07.client;

import com.jmunoz.common.AbstractHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ExternalServiceClient extends AbstractHttpClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalServiceClient.class);

    // Como somos los desarrolladores de ExternalServiceClient, y pensamos que nuestros subscribers
    // pueden hacer un mal uso del thread, y no podrá hacer las operaciones IO,
    // podemos añadir un operador publishOn() que nos permita cambiar el thread de ejecución.
    public Mono<String> getProductName(int productId) {
        return this.httpClient.get()
                .uri("/demo01/product/" + productId)
                .responseContent()
                .asString()
                .doOnNext(m -> log.info("next: {}", m))
                .next()
                // Comentar para ver que Lec06EventLoopIssueFix.java tarda 5 segundos en completarse.
                .publishOn(Schedulers.boundedElastic());
    }
}
