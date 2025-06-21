package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import com.jmunoz.sec03.client.ExternalServiceClient;

// Para probar Non-Blocking IO con flujo de mensajes,
// tenemos que asegurarnos de que external-services.jar está ejecutándose.
public class Lec08NonBlockingIOStreamingMessages {

    public static void main(String[] args) {
        var client = new ExternalServiceClient();

        // Tenemos dos subscriber para que veamos lo eficientemente que se transmiten los mensajes desde una
        // aplicación a otra, sin que se bloquee el hilo de ejecución.
        client.getNames()
                .subscribe(Util.subscriber("sub1"));

        client.getNames()
                .subscribe(Util.subscriber("sub2"));

        // Como es no bloqueante, el thread main terminará inmediatamente,
        // y por eso lo bloqueamos.
        Util.sleepSeconds(6);
    }
}
