package com.jmunoz.sec02;

import com.jmunoz.common.Util;
import com.jmunoz.sec02.client.ExternalServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Ejecutar external-services.jar con el comando java -jar external-services.jar
public class Lec11NonBlockingIO {

    private static final Logger log = LoggerFactory.getLogger(Lec11NonBlockingIO.class);

    public static void main(String[] args) {

        var client = new ExternalServiceClient();

        log.info("starting");
        
        // La gracia de realizar llamadas IO no bloqueantes es esta.
        // Mandamos 5 peticiones. Para ello creamos 5 publishers y nos suscribimos a cada uno de ellos.
        // Tarda un segundo en obtener la respuesta ¡¡de todos!! y solo un thread (que no es el main) hizo el trabajo.
        //
        // En programación tradicional, si tuviéramos 100 peticiones, tardaría 100sg en devolvernos todas las respuestas.
        // Esto es porque en cada petición, el bucle espera a que se devuelva el resultado.
        // Con peticiones IO no bloqueantes, tardaría poco más de 1sg porque no hay bloqueo. Se mandan todas las peticiones.
        //
        // Eso sí, no nos da la respuesta ordenada. En mi caso primero me ha dado la respuesta del productId-1,
        // luego la del productId-5... Esto es porque mandamos las peticiones más o menos al mismo tiempo,
        // con lo que las respuestas nos llegarán antes o después dependiendo de como se procesen.
        // Pero, si es necesario, si se puede respetar el orden. Lo veremos más adelante.
        for (int i = 1; i <= 5; i++) {
            client.getProductName(i)
                    .subscribe(Util.subscriber());
        }

        // Como es IO no bloqueante, nuestro main thread termina antes de que nos dé la respuesta.
        // Tenemos que bloquear el main thread un tiempo para poder ver el resultado.
        // Indicamos 2 sg porque sabemos que la respuesta nos tarda 1 sg.
        // Esto en proyectos reales, por supuesto que no hace falta. Usando Spring WebFlux la aplicación
        // se ejecuta en modo server.
        Util.sleepSeconds(2);
    }
}
