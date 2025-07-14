package com.jmunoz.sec07;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
    publish on for downstream
    subscribe on for upstream
*/
public class Lec07PublishOnSubscribeOn {

    public static final Logger log = LoggerFactory.getLogger(Lec07PublishOnSubscribeOn.class);

    public static void main(String[] args) {
        var flux = Flux.create(sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating {}", i);
                        sink.next(i);
                    }

                    sink.complete();
                })
                .publishOn(Schedulers.parallel())
                .doOnNext(v -> log.info("value: {}", v))
                .doFirst(() -> log.info("first1"))
                .subscribeOn(Schedulers.boundedElastic())
                .doFirst(() -> log.info("first2"));


        Runnable runnable = () -> flux.subscribe(Util.subscriber());
        Thread.ofPlatform().start(runnable);

        // Como ahora se va a ejecutar en un hilo diferente, debemos esperar un poco.
        Util.sleepSeconds(2);
    }
}
