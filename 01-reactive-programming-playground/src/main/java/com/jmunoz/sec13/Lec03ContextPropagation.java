package com.jmunoz.sec13;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

public class Lec03ContextPropagation {

    private static final Logger log = LoggerFactory.getLogger(Lec03ContextPropagation.class);

    public static void main(String[] args) {
        getWelcomeMessage()
                // Propagamos la información del context al producer1.
                // Limpiamos el context para el producer2 porque no queremos que lo obtenga.
                .concatWith(Flux.merge(producer1(), producer2().contextWrite(ctx -> Context.empty())))
                .contextWrite(Context.of("user", "sam"))
                .subscribe(Util.subscriber());

        Util.sleepSeconds(2);
    }

    // Vamos a asumir que tenemos varios producers.
    private static Mono<String> getWelcomeMessage() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey("user")) {
                return Mono.just("Welcome %s".formatted(ctx.get("user").toString()));
            }

            return Mono.error(new RuntimeException("unauthenticated"));
        });
    }

    private static Mono<String> producer1() {
        // Este publisher usa su propio thread pool.
        return Mono.<String>deferContextual(ctx -> {
            log.info("producer1: {}", ctx);
            return Mono.empty();
        })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private static Mono<String> producer2() {
        // Este publisher usa su propio thread pool.
        return Mono.<String>deferContextual(ctx -> {
                    log.info("producer2: {}", ctx);
                    return Mono.empty();
                })
                .subscribeOn(Schedulers.parallel());
    }
}
