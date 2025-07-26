package com.jmunoz.sec09.applications;

import reactor.core.publisher.Mono;

import java.util.Map;

/*
    Solo para demo.
    Imaginar paymentService como una aplicación, tiene un endpoint.
    Esta es una clase cliente que representa la llamada al endpoint (ID request).
*/
public class PaymentService {

    private static final Map<Integer, Integer> userBalanceTable = Map.of(
            1, 100,
            2, 200,
            3, 300
    );

    public static Mono<Integer> getUserBalance(Integer userId) {
        return Mono.fromSupplier(() -> userBalanceTable.get(userId));
    }
}
