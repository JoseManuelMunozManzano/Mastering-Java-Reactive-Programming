package com.jmunoz.sec09.client;

import com.jmunoz.common.AbstractHttpClient;
import com.jmunoz.sec09.assignments.Product;
import reactor.core.publisher.Mono;

public class ExternalServiceClient extends AbstractHttpClient {

    public Mono<Product> getProduct(int id) {
        // Uso la t de tuple para tener en cuenta que es una tuple.
        return Mono.zip(getProductName(id), getPrice(id), getReview(id))
                .map(t -> new Product(id, t.getT1(), t.getT2(), t.getT3()));
    }

    private Mono<String> getProductName(int id) {
        return this.httpClient.get()
                .uri("/demo05/product/" + id)
                .responseContent()
                .asString()
                .next();
    }

    private Mono<String> getPrice(int id) {
        return this.httpClient.get()
                .uri("/demo05/price/" + id)
                .responseContent()
                .asString()
                .next();
    }

    private Mono<String> getReview(int id) {
        return this.httpClient.get()
                .uri("/demo05/review/" + id)
                .responseContent()
                .asString()
                .next();
    }
}
