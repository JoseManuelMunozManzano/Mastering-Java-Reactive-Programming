package com.jmunoz.sec03.assignment;

import com.jmunoz.common.Util;
import com.jmunoz.sec03.client.ExternalServiceClient;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Assignment {

    private static final Logger log = LoggerFactory.getLogger(Assignment.class);

    public static void main(String[] args) {
        var client = new ExternalServiceClient();
        // Saldo inicial del usuario.
        var balance = 1000;

        client.getPriceChanges()
                .subscribe(stockPriceSubscriber(balance));

        // Como es no bloqueante, el thread main terminará inmediatamente,
        // y por eso lo bloqueamos.
        Util.sleepSeconds(21);
    }

    private static Subscriber<? super Integer> stockPriceSubscriber(int balance) {
        return new Subscriber<>() {
            private int currentBalance = balance;
            private int quantity = 0;
            private int profit = 0;
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                // Solicitar todos los elementos disponibles
                this.subscription = subscription;
                this.subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer price) {
                log.info("Stock Price: {}", price);
                if (price < 90 && currentBalance >= price) {
                    quantity++;
                    currentBalance -= price;
                    log.info("Current Balance: {}", currentBalance);
                } else if (price > 110 && quantity > 0) {
                    profit = price * quantity;
                    onComplete();
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("Error: {}", t.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("Current Balance: {}, Profit: {}, Total: {}", currentBalance, profit, (currentBalance + profit));
                subscription.cancel();
            }
        };
    }
}
