package com.jmunoz.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec02EmptyErrorTest {

    // Esta sería nuestra app.
    // Imaginemos que tenemos un Publisher.
    Mono<String> getUsername(int userId) {
        return switch (userId) {
            case 1 -> Mono.just("sam");
            case 2 -> Mono.empty();
            default -> Mono.error(new RuntimeException("Invalid input"));
        };
    }

    // Este test no es el importante de esta clase, pero también es necesario para partir de algo correcto.
    @Test
    public void userTest() {
        StepVerifier.create(getUsername(1))
                .expectNext("sam")
                .expectComplete()
                // No olvidar verify()!!!!
                .verify();
    }

    // Este es nuestro test empty.
    // Debemos indicar que el publisher nos pasa la señal empty o onComplete()
    // Esto se consigue indicando solo el méto-do expectComplete()
    @Test
    public void emptyTest() {
        StepVerifier.create(getUsername(2))
                .expectComplete()
                // No olvidar verify()!!!!
                .verify();
    }

    // Este es uno de nuestros tests de error. Vamos a ver diferentes opciones.
    // El error que devuelve el publisher es un RuntimeException.
    // Cuando esperamos una señal de error del publisher, usaremos el méto-do expectError(), que
    // permite cualquier error.
    @Test
    public void errorTest1() {
        StepVerifier.create(getUsername(3))
                .expectError()
                // No olvidar verify()!!!!
                .verify();
    }

    // Cuando queremos ser muy específicos con el tipo de error que esperamos que emita el publisher, lo
    // indicaremos como argumento del méto-do expectError()
    @Test
    public void errorTest2() {
        StepVerifier.create(getUsername(3))
                .expectError(RuntimeException.class)
                // No olvidar verify()!!!!
                .verify();
    }

    // Cuando queremos validar el mensaje de error que emite el publisher, usaremos el méto-do
    // expectErrorMessage(), indicando como parámetro el mensaje.
    @Test
    public void errorTest3() {
        StepVerifier.create(getUsername(3))
                .expectErrorMessage("Invalid input")
                // No olvidar verify()!!!!
                .verify();
    }

    // Cuando queremos ser muy específicos tanto con el tipo de error que esperamos que emita el publisher,
    // como con el mensaje de error que emite.
    // Para ello, usaremos el Consumer consumeErrorWith() que acepta la excepción y podremos hacer aserciones.
    //
    // Notar que también existe consumeNextWith() para poder hacer nuestras propias aserciones cuando obtenemos
    // un valor emitido por el publisher.
    @Test
    public void errorTest4() {
        StepVerifier.create(getUsername(3))
                .consumeErrorWith(ex -> {
                    Assertions.assertEquals(RuntimeException.class, ex.getClass());
                    Assertions.assertEquals("Invalid input", ex.getMessage());
                })
                // No olvidar verify()!!!!
                .verify();
    }
}
