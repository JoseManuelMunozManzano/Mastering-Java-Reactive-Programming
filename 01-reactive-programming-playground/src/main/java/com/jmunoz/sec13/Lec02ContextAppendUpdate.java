package com.jmunoz.sec13;

import com.jmunoz.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

// El context es un mapa inmutable, pero podemos añadir/actualizar su info devolviendo un nuevo objeto.
public class Lec02ContextAppendUpdate {

    private static final Logger log = LoggerFactory.getLogger(Lec02ContextAppendUpdate.class);

    public static void main(String[] args) {

        // Añadir al context.
//        append();

        // Modificar el context.
        update();
    }

    private static void update() {
        // Para la modificación del context, no olvidar que vamos de abajo a arriba.
        // Es decir, primero el context obtiene la key user con value sam, luego las keys a, c, y e,
        // luego se elimina la key c, y luego se actualiza el value de la key user.
        getWelcomeMessage()
                // Utilizando una function para dar un nuevo context
                // para modificar el map existente (pero recordar que siempre devuelve un nuevo objeto)
                // Actualizamos el value de la key user a mayúsculas.
                .contextWrite(ctx -> ctx.put("user", ctx.get("user").toString().toUpperCase()))
                // Eliminamos la key del context.
                .contextWrite(ctx -> ctx.delete("c"))
                // Damos un nuevo map (elimina to-do lo que haya anterior)
//                .contextWrite(ctx -> Context.of("user", "mike"))
                // Vaciamos el map.
//                .contextWrite(ctx -> Context.empty())
                .contextWrite(Context.of("a", "b").put("c", "d").put("e", "f"))
                .contextWrite(Context.of("user", "sam"))
                .subscribe(Util.subscriber());
    }

    private static void append() {
        getWelcomeMessage()
                // Primera forma de añadir al context.
                .contextWrite(Context.of("a", "b"))
                .contextWrite(Context.of("user", "sam"))
                // Segunda forma de añadir al context, encadenando con .put()
                .contextWrite(Context.of("c", "d").put("e", "f").put("g", "h"))
                // Otras formas de añadir al context usando .putAll() o .putAllMap()
                .subscribe(Util.subscriber());
    }

    private static Mono<String> getWelcomeMessage() {
        return Mono.deferContextual(ctx -> {
            log.info("{}", ctx);
            if (ctx.hasKey("user")) {
                return Mono.just("Welcome %s".formatted(ctx.get("user").toString()));
            }

            return Mono.error(new RuntimeException("unauthenticated"));
        });
    }
}
