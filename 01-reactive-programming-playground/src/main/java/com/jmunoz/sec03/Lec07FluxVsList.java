package com.jmunoz.sec03;

import com.jmunoz.common.Util;
import com.jmunoz.sec01.subscriber.SubscriberImpl;
import com.jmunoz.sec03.helper.NameGenerator;

public class Lec07FluxVsList {

    public static void main(String[] args) {

        // Forma tradicional con List.
        // Cuando pasan 10 segundos, se imprime el resultado, es decir, estamos bloqueados 10 segundos.
        //
//        var list = NameGenerator.getNamesList(10);
//        System.out.println(list);

        // Forma reactiva con Flux.
        // Se imprime el resultado a medida que se generan los nombres, no hay bloqueo, es responsive.
        //
//        NameGenerator.getNamesFlux(10)
//                .subscribe(Util.subscriber());

        // Vemos de nuevo la forma reactiva, pero usando el subscriber que hicimos en el paquete sec01.
        // Aunque el publisher emite 10 elementos, el subscriber solo solicita 3.
        // Es decir, el subscriber puede cancelar cuando quiera.
        // Esto no se puede hacer en la forma tradicional con List.
        //
        var subscriber = new SubscriberImpl();
        NameGenerator.getNamesFlux(10)
                .subscribe(subscriber);
        subscriber.getSubscription().request(3);
        subscriber.getSubscription().cancel();
    }
}
