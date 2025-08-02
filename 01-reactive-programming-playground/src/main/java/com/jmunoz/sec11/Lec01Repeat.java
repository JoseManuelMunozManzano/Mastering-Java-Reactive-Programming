package com.jmunoz.sec11;

import com.jmunoz.common.Util;
import reactor.core.publisher.Mono;

// El operador repeat se resubscribe cuando aparece la señal onComplete()
// No hace nada si la señal es onError()
public class Lec01Repeat {

    public static void main(String[] args) {
        // En la vida real, será una operación IO no bloqueante.
        var mono = Mono.fromSupplier(() -> Util.faker().country().name());
        var subscriber = Util.subscriber();

        // Usando el operador repeat(), cuando obtengamos la señal onComplete(), se resubscribe.
        // Podemos indicar el número de veces que queremos que se resubscriba, teniendo en cuenta
        // que para un valor 3 obtendremos 4 valores, el primero que sería el normal, y luego 3 veces que queremos repetir.
        //
        // Usando este operador, ya NO ES UN MONO, sino FLUX porque el subscriber recibe más de un item.
        // Al ser un Flux, gestiona automáticamente el back pressure.
        // Si añadimos el operador repeat() a un Flux, obviamente sigue siendo un Flux.
        mono.repeat(3)
                .subscribe(subscriber);

        // Hasta ahora, si queríamos obtener la data de nuevo, teníamos que volver a subscribirnos.
        //
        // De hecho, este bucle puede dar problemas.
        // Funciona en este caso porque to-do lo tenemos en memoria, pero en la vida real el publisher
        // va a ser operaciones IO no bloqueantes, por lo que, incluso antes de obtener la respuesta de la primera
        // iteración, se va a estar ejecutando la segunda y tercera iteración, concurrentemente, cosa que no querremos.
        // Tampoco se gestiona, usando un bucle, el back pressure.
        //
        // De ahí la utilidad del operador repeat(), que solo repite tras obtener la señal onComplete(), en secuencia.
        //
//        for (int i = 0; i < 3; i++) {
//            mono.subscribe(subscriber);
//        }
    }
}
