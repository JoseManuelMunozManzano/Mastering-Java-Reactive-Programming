package com.jmunoz.sec11;

import com.jmunoz.common.Util;
import com.jmunoz.sec11.client.ExternalServiceClient;
import com.jmunoz.sec11.client.ServerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

// No olvidar ejecutar el servicio externo: java -jar external-services.jar
public class Lec03ExternalServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(Lec03ExternalServiceDemo.class);

    public static void main(String[] args) {

//        repeat();

        retry();

        Util.sleepSeconds(60);
    }

    private static void repeat() {
        var client = new ExternalServiceClient();

        // Llamamos solo una vez, pero el operador repeat() repite la petición las veces necesarias.
        client.getCountry()
                .repeat()
                .takeUntil(c -> c.equalsIgnoreCase("canada"))
                .subscribe(Util.subscriber());
    }

    private static void retry() {
        var client = new ExternalServiceClient();

        // Llamamos solo una vez, pero el operador retryWhen() repite la petición si es un error de server.
        // El id 1 devuelve siempre BadRequest.
        // El id 2 puede devolver un producto, BadRequest o ServerInternalError.
        client.getProductName(2)
                .retryWhen(retryOnServerError())
                .subscribe(Util.subscriber());
    }

    private static Retry retryOnServerError() {
        // Reintentamos un máximo de 20 veces, de segundo en segundo.
        // No reintentamos si el error es de tipo ClientError, solo si es de tipo ServerError.
        return Retry.fixedDelay(20, Duration.ofSeconds(1))
                .filter(ex -> ServerError.class.equals(ex.getClass()))
                .doBeforeRetry(rs -> log.info("retrying {}", rs.failure().getMessage()));
    }
}
