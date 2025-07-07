package com.jmunoz.sec05.assignment;

import com.jmunoz.common.Util;
import com.jmunoz.sec05.client.ExternalServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    - Tenemos 4 id de producto: 1, 2, 3 y 4.
    - Usar productName usando product-service.
    - Timeout de 2 segundos.
        - Llamar a un servicio fallback de timeout para obtener el nombre del producto.
    - Llamar a un servicio fallback de empty para obtener el nombre del producto en caso de que no se encuentre el producto.
    - Permitir a la clase cliente abstracta el manejo de timeout/empty
        - client.getProductName(1)

- Para este ejercicio, arrancar el proyecto `java -jar external-services.jar` e ir al navegador a `http://localhost:7070/webjars/swagger-ui/index.html`.
- Usaremos los tres endpoints que existen en `demo03/`.
*/
public class Assignment {

    private static final Logger log = LoggerFactory.getLogger(Assignment.class);

    public static void main(String[] args) {
        var client = new ExternalServiceClient();

        for (int i = 1; i < 5; i++) {
            client.getProductName(i).subscribe(Util.subscriber());
        }

        Util.sleepSeconds(5);
    }
}
