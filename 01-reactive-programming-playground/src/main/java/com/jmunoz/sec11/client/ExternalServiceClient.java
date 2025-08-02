package com.jmunoz.sec11.client;

import com.jmunoz.common.AbstractHttpClient;
import com.jmunoz.sec09.assignments.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClientResponse;

/*
- Arrancar el proyecto `java -jar external-services.jar` e ir al navegador a `http://localhost:7070/webjars/swagger-ui/index.html`.
  - Usaremos los endpoints `demo06/country` para ver como funciona `repeat()` y `demo06/product/{id}` para ver como funciona `retry()`, donde el id 1 devuelve `Bad Request` y el id 2 devuelve un error random o a veces funciona.
*/
public class ExternalServiceClient extends AbstractHttpClient {

    // Este endpoint puede devolver BadRequest, Internal Server Error o un producto correcto.
    public Mono<String> getProductName(int productId) {
        return get("/demo06/product/" + productId);
    }

    // Este endpoint siempre recupera información correcta.
    public Mono<String> getCountry() {
        return get("/demo06/country");
    }

    // El problema es que Reactor no sabe que el 400 es BadRequest o 500 es Internal Server Error.
    // Esto es porque es una herramienta de muy bajo nivel, lo ve to-do como un buffer de bytes que nos devuelve (response header).
    // Es nuestra responsabilidad interpretar lo que nos devuelve como malo o bueno.
    // Veremos que el framework Spring usa Reactor por debajo y, basado en el response header, lanzará el error apropiado.
    // Pero aquí no estamos usando el framework, así que tenemos que hacerlo nosotros, obtener el response header y lanzar
    // el error nosotros.
    // Para ello, usamos response() al que tenemos que pasarle una BiFunction con HttpClientResponse, ByteBufFlux
    private Mono<String> get(String path) {
        return this.httpClient.get()
                .uri(path)
                .response(this::toResponse)
                .next();        // Convierte a Mono.
    }

    // Para los errores 400 y 500, creamos excepciones personalizadas, ClientError y ServerError.
    private Flux<String> toResponse(HttpClientResponse httpClientResponse, ByteBufFlux byteBufFlux) {
        return switch (httpClientResponse.status().code()) {
            case 200 -> byteBufFlux.asString();
            case 400 -> Flux.error(new ClientError());
            default -> Flux.error(new ServerError());
        };
    }
}
