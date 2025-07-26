package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import com.jmunoz.sec09.client.ExternalServiceClient;
import reactor.core.publisher.Flux;

public class Lec13ConcatMap {

    public static void main(String[] args) {
        var client = new ExternalServiceClient();

        // Obtener elementos de forma secuencial.
        Flux.range(1, 10)
                .concatMap(client::getProduct)
                .subscribe(Util.subscriber());

        Util.sleepSeconds(12);
    }
}
