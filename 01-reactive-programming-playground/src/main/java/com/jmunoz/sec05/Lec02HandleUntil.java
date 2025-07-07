package com.jmunoz.sec05;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

// Ejemplo de uso de handle para filtrar hasta que se encuentre un elemento específico.
public class Lec02HandleUntil {

    public static void main(String[] args) {
        Flux.<String>generate(synchronousSink -> {
                    synchronousSink.next(Util.faker().country().name());
                })
                .handle((item, sink) -> {
                    sink.next(item);
                    if (item.equalsIgnoreCase("canada")) {
                        sink.complete();
                    }
                })
                .subscribe(Util.subscriber());
    }
}
