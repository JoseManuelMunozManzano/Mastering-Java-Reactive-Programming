package com.jmunoz.sec09;

import com.jmunoz.common.Util;
import com.jmunoz.sec09.applications.Order;
import com.jmunoz.sec09.applications.OrderService;
import com.jmunoz.sec09.applications.User;
import com.jmunoz.sec09.applications.UserService;
import reactor.core.publisher.Flux;

/*
    Llamadas IO secuenciales no bloqueantes.
    flatMap se usa para aplanar el publisher interno / para subscribirse a un publisher interno.
*/
public class Lec11FluxFlatMap {

    public static void main(String[] args) {

        // Obtener todos los pedidos de order service.
        // Esto tenemos que hacerlo en varios pasos, primero obtener todos los usuarios,
        // y para cada usuario, obtener sus pedidos.
        //
        // De nuevo usamos map() intencionadamente para obtener el tipo.
        // En este caso, vemos que el resultado es un Flux<Flux<Order>>.
        //
//        Flux<Flux<Order>> flux = UserService.getAllUsers()
//                .map(user -> OrderService.getUserOrders(user.id()));

        // Necesitamos un flatMap() que se subscriba al publisher interno y lo aplane.
        // Siendo el publisher externo un Flux, podemos usar flatMap() tanto si el
        // publisher interno es un Mono o un Flux.
        // Indicar que el primer map solo se usa para obtener el id del usuario, es la operación en memoria
        // que puede hacerse sin problemas usando un map.
        //
        // Vemos que flatMap() acepta un parámetro para indicar la cantidad máxima de peticiones concurrentes.
        // Si no se indica, el valor por defecto actual es de 256, que es el tamaño de la cola interna de Reactor.
        UserService.getAllUsers()
                .map(User::id)
                .flatMap(OrderService::getUserOrders, 1)
                .subscribe(Util.subscriber());

        Util.sleepSeconds(3);
    }
}
