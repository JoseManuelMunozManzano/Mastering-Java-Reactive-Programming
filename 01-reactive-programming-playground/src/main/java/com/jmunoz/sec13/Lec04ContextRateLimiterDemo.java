package com.jmunoz.sec13;

import com.jmunoz.common.Util;
import com.jmunoz.sec13.client.ExternalServiceClient;
import reactor.util.context.Context;

// Para ejecutar esta prueba, tenemos que ejecutar nuestro external service
// java -jar external-services.jar
public class Lec04ContextRateLimiterDemo {

    public static void main(String[] args) {

        var client = new ExternalServiceClient();

        // Usando un bucle for realizamos 10 peticiones.
        // Si tenemos que pagarlas, es mejor limitar este número de llamadas.
        // Para eso hemos modificado nuestra llamada al servicio externo. Ver ExternalServiceClient.java.
        for (int i = 0; i < 20; i++) {
            client.getBook()
                    // Indicamos en el context el nombre del usuario.
                    // Con el user sam, de las 20 llamadas, solo obtenemos 2 repuestas cada 5sg, porque es un user standard.
//                    .contextWrite(Context.of("user", "sam"))
                    // Con el user mike, de las 20 llamadas, solo obtenemos 3 repuestas cada 5sg, porque es un user prime.
                    .contextWrite(Context.of("user", "mike"))
                    // ¿Qué pasa si el user no pertenece a ninguna categoría? No hará ninguna petición al servicio externo.
//                    .contextWrite(Context.of("user", "jake"))
                    // ¿Qué pasa si ponemos la categoría? Como no hay user, en UserService devolvemos el contexto vacío
                    // y no se hará ninguna petición.
//                    .contextWrite(Context.of("category", "prime"))
                    .subscribe(Util.subscriber());

            // Tras cada llamada bloqueamos 1sg, porque si no las 20 llamadas las hace muy rápido y no
            // da tiempo a probar la actualización de los intentos máximos por tipo de categoría de usuario cada 5sg.
            Util.sleepSeconds(1);
        }

        // Bloqueamos el hilo principal.
        Util.sleepSeconds(5);
    }
}
