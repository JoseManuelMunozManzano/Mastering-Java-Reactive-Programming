package com.jmunoz.common;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.LoopResources;

public abstract class AbstractHttpClient {

    private static final String BASE_URL = "http://localhost:7070";
    protected final HttpClient httpClient;

    public AbstractHttpClient() {
        // Vamos a sobreescribir la creación porque queremos actualizar loop resources.
        // Los loop resources son básicamente un tipo de helper para crear el event loop group.
        // Loop resources serían el manager de event loop group y por debajo lo crean.
        //
        // Lo que hace Reactor Netty es crear un thread por CPU, pero no queremos eso, solo queremos un thread para
        // ver como un thread puede hacer to-do el trabajo. Ese es el propósito de esta demo.
        // El true indica que es un daemon thread.
        var loopResources = LoopResources.create("jose", 1, true);
        this.httpClient = HttpClient.create().runOn(loopResources).baseUrl(BASE_URL);
    }
}
