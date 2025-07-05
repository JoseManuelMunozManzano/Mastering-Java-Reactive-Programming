package com.jmunoz.sec04.helper;

import com.jmunoz.common.Util;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public class NameGenerator implements Consumer<FluxSink<String>> {

    private FluxSink<String> sink;

    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        this.sink = stringFluxSink;
    }

    // Esta es una idea que no es robusta, sirve de ejemplo.
    // No sabemos si sink es null o no, si se ha llamado a accept antes de llamar a generate().
    // Tampoco estamos indicando la señal complete(), por ejemplo podríamos emitir 100 items. Entonces,
    // podríamos tener un contador e ir incrementándolo hasta llegar a 100 y luego llamar a complete().
    public void generate() {
        this.sink.next(Util.faker().name().firstName());
    }
}
