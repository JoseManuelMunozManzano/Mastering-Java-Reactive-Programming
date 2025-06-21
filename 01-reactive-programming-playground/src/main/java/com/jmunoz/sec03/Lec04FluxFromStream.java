package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

import java.util.List;

public class Lec04FluxFromStream {

    public static void main(String[] args) {
        var list = List.of(1, 2, 3, 4);
        var stream = list.stream();

        var flux = Flux.fromStream(stream);

        flux.subscribe(Util.subscriber("sub1"));

        // IMPORTANTE: El stream ya se ha consumido, por lo que no se puede volver a usar.
        // Este comportamiento es el propio de los streams en Java.
        flux.subscribe(Util.subscriber("sub2"));

        // Si tenemos varios subscribers, debemos proveer el supplier del stream, es decir,
        // tenemos que crear un nuevo stream para cada subscriber.
        var flux2 = Flux.fromStream(list::stream);
        flux2.subscribe(Util.subscriber("sub3"));
        flux2.subscribe(Util.subscriber("sub4"));

    }
}
