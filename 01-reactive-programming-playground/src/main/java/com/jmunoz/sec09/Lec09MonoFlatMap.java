package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import com.jmunoz.sec09.applications.PaymentService;
import com.jmunoz.sec09.applications.UserService;
import reactor.core.publisher.Mono;

/*
    Vemos como funciona Mono.flatMap().

    Llamadas IO secuenciales no bloqueantes.
    flatMap se usa para aplanar el publisher interno / para subscribirse a un publisher interno.
*/
public class Lec09MonoFlatMap {

    public static void main(String[] args) {
        // Tenemos username y queremos obtener el balance del usuario.

        // Solo map no funciona.
        // El problema es que se devuelve un Mono<Mono<Integer>> y no un Mono<Integer>.
        // Si nos subscribimos obtendremos como resultado el publisher interno.
        //
        // map() es bueno para cálculos en memoria, por ejemplo, tenemos el id y queremos concatenarle un string.
        // Pero si obtenemos otro mono, entonces tendremos este problema.
        //
//        UserService.getUserId("sam")
//                .map(userId -> PaymentService.getUserBalance(userId))
//                .subscribe(Util.subscriber());

        // Aquí es donde entra en juego flatMap().
        // Con flatMap() nos subscribimos al publisher interno y obtenemos el resultado directamente.
        UserService.getUserId("sam")
                .flatMap(PaymentService::getUserBalance)
                .subscribe(Util.subscriber());

    }
}
