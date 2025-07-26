package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import com.jmunoz.sec09.applications.OrderService;
import com.jmunoz.sec09.applications.UserService;

/*
    Llamadas IO secuenciales no bloqueantes.
    flatMap se usa para aplanar el publisher interno / para subscribirse a un publisher interno.
    Se supone que Mono es para 1 item - ¿Qué ocurre si flatMap devuelve varios items?
*/
public class Lec10MonoFlatMapMany {

    public static void main(String[] args) {
        /*
            Tenemos username y queremos obtener todos los pedidos del usuario.

            Si lo hacemos usando map() (mal de forma intencionada), veremos que obtenemos un tipo Mono<Flux<Order>>.
            No es un Mono<Mono<Order>> porque OrderService.getUserOrders() devuelve un Flux<Order>.

            Volviendo a flatMap(), este operador asume que el publisher interno es un tipo Mono.
            Como no es el caso, flattMap() no es adecuado aquí.
        */
//        Mono<Flux<Order>> sam = UserService.getUserId("sam")
//                .map(userId -> OrderService.getUserOrders(userId));

        // La solución es usar flatMapMany() para subscribirse al publisher interno que devuelve varios items.
        UserService.getUserId("mike")
                .flatMapMany(OrderService::getUserOrders)
                .subscribe(Util.subscriber());


        Util.sleepSeconds(3);
    }
}
