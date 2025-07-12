package com.jmunoz.sec06.client;

import com.jmunoz.common.AbstractHttpClient;
import com.jmunoz.sec06.assignment.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Objects;

public class ExternalServiceClient extends AbstractHttpClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalServiceClient.class);
    private Flux<Order> orderFlux;

    // Así nos aseguramos de que el flujo de órdenes se crea una sola vez.
    // Esto es muy importante porque para que emita, necesitamos que haya al menos dos suscriptores al MISMO publisher.
    public Flux<Order> orderStream() {
        if (Objects.isNull(this.orderFlux)) {
            this.orderFlux = getOrderStream();
        }
        return this.orderFlux;
    }

    private Flux<Order> getOrderStream() {
        return this.httpClient.get()
                .uri("/demo04/orders/stream")
                .responseContent()
                .asString()
                .map(Order::of)
                .doOnNext(o -> log.info("{}", o))
                .publish()
                .refCount(2);
    }
}
