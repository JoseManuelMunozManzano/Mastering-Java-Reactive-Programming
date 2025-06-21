package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import reactor.core.publisher.Flux;

// just es muy útil cuando tenemos data en memoria.
public class Lec01FluxJust {

    public static void main(String[] args) {

        Flux.just(1, 2, 3, "José")
                .subscribe(Util.subscriber());
    }
}
