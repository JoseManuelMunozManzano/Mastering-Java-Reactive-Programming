package com.jmunoz.sec07;

import com.jmunoz.common.Util;
import com.jmunoz.sec07.client.ExternalServiceClient;

// Ejemplo para ver la importancia de los Schedulers.
public class Lec06EventLoopIssueFix {

    public static void main(String[] args) {
        var client = new ExternalServiceClient();

        for (int i = 1; i <= 5; i++) {
            client.getProductName(i)
                    // Al añadir este procesamiento, el proceso pasa de tardar 1 segundo a 5 segundos, 1 por cada producto.
                    // Y obtenemos los nombres de producto secuencialmente.
                    // Esto es porque el event loop thread nos da el product name y le pedimos al event loop thread que realice ese procesamiento
                    // que es bloqueante, y no puede hacer las tareas IO.
                    //
                    // Esto se puede corregir si somos los desarrolladores de ExternalServiceClient, y pensamos que nuestros subscribers
                    // pueden hacer un mal uso del thread, y no podrá hacer las operaciones IO, añadiendo un operador publishOn() que nos permita cambiar el thread de ejecución.
                    // Ir a ExternalServiceClient.java para ver el cambio.
                    // Con ese cambio, obtenemos los 5 productos a la vez como antes, ya que el event loop thread esta centrado en la parte IO, que es lo que se supone que tiene que hacer.
                    .map(Lec06EventLoopIssueFix::process)
                    .subscribe(Util.subscriber());
        }

        Util.sleepSeconds(20);
    }

    // Suponemos que este procesamiento tarda 1 segundo en completarse.
    private static String process(String input) {
        Util.sleepSeconds(1);
        return input + "-processed";
    }
}
