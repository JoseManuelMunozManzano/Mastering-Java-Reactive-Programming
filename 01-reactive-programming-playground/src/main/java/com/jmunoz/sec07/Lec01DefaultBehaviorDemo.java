package com.jmunoz.sec07;

import com.jmunoz.common.Util;
import com.jmunoz.sec06.Lec04HotPublisherCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

// Por defecto, el hilo actual (current thread) hace to-do el trabajo.
public class Lec01DefaultBehaviorDemo {

    private static final Logger log = LoggerFactory.getLogger(Lec01DefaultBehaviorDemo.class);

    public static void main(String[] args) {

        var flux = Flux.create(sink -> {
                    for (int i = 1; i < 3; i++) {
                        log.info("generating {}", i);
                        sink.next(i);
                    }

                    sink.complete();
                })
                .doOnNext(v -> log.info("value: {}", v));

        // Cada subscriber obtendrá su propio sink.
        // To-do lo hace el hilo principal (main thread).
        //
        // flux.subscribe(Util.subscriber("sub1"));
        // flux.subscribe(Util.subscriber("sub2"));

        // Ahora to-do lo hace un hilo diferente.
        Runnable runnable = () -> flux.subscribe(Util.subscriber("sub1"));
        Thread.ofPlatform().start(runnable);
    }
}
