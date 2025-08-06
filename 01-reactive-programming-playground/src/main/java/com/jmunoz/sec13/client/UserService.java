package com.jmunoz.sec13.client;

import reactor.util.context.Context;

import java.util.Map;
import java.util.function.Function;

// Como es una demo, se usa mucho static, pero en la vida real serían singleton Spring Bean.
public class UserService {

    private static final Map<String, String> USER_CATEGORY = Map.of(
            "sam", "standard",
            "mike", "prime"
    );

    static Function<Context, Context> userCategoryContext() {
        // Si en el contex tenemos la key user, lo buscamos en USER_CATEGORY.
        // Si lo encontramos obtenemos su value y devolvemos el nuevo context.
        // Si no lo encontramos, devolvemos el context vacío. Esto es para evitar que, si nos mandan la categoría,
        // sin el usuario, haga la llamada, ya que lo que necesitamos para hacer la llamada es la categoría.
        return ctx -> ctx.<String>getOrEmpty("user")
                .filter(USER_CATEGORY::containsKey)
                .map(USER_CATEGORY::get)
                .map(category -> ctx.put("category", category))
                .orElse(Context.empty());
    }
}
