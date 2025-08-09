package com.jmunoz.tests;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

public class Lec07ScenarioNameTest {

    // Esta sería nuestra app.
    // Imaginemos que tenemos este Publisher.
    private Flux<Integer> getItems() {
        return Flux.range(1, 3);
    }

    // Imaginemos que esperamos solo los items 1 y 2 y luego la señal onComplete()
    // Este test va a fallar.
    //
    // Podemos añadir una descripción a este test que falla, usando unas opciones con
    // StepVerifierOptions.create().scenarioName(), al que indicamos un String.
    // Luego, pasaremos estas opciones como segundo parámetro al méto-do create().
    //
    // Ahora, el test sigue fallando, pero aparece una descripción.
    // Esto es muy útil cuando tenemos un proyecto muy grande y tenemos muchos tests, para saber rápidamente
    // cuál ha fallado.
    @Test
    public void scenarioNameTest1() {
        var options = StepVerifierOptions.create().scenarioName("1 to 3 items test 1");

        StepVerifier.create(getItems(), options)
                .expectNext(1, 2)
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }

    // También podemos añadir una descripción de nivel de paso.
    //
    // Imaginemos que indicamos que esperamos los valores 2 y 4.
    // Podemos indicar el méto-do as() al que hay que pasarle un String con la descripción del nivel de paso.
    // Cuando este test falle, veremos las dos descripciones, la del scenarioName y la del nivel de paso,
    // y sabremos qué paso del test es el que ha fallado.
    @Test
    public void scenarioNameTest2() {
        var options = StepVerifierOptions.create().scenarioName("1 to 3 items test 2");

        StepVerifier.create(getItems(), options)
                .expectNext(1)
                .as("first item should be 1")
                .expectNext(2, 4)
                .as("then 2 and 4")
                .expectComplete()
                // No olvidar verify() para que realmente se ejecute el StepVerifier!!
                .verify();
    }
}
