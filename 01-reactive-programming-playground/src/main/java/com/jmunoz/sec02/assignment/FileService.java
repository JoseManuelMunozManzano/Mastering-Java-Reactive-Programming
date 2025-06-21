package com.jmunoz.sec02.assignment;

import reactor.core.publisher.Mono;

public interface FileService {

    Mono<String> read(String filename);
    Mono<Void> write(String filename, String content);
    Mono<Void> delete(String filename);
}
