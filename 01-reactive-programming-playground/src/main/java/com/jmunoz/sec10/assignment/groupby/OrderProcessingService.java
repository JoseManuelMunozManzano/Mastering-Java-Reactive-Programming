package com.jmunoz.sec10.assignment.groupby;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

// Solo para el ejercicio.
// En la vida real, podríamos tener clases separadas para cada categoría.
public class OrderProcessingService {

    private static final Map<String, UnaryOperator<Flux<PurchaseOrder>>> PROCESSOR_MAP = Map.of(
            "Kids", kidsProcessing(),
            "Automotive", automotiveProcessing()
    );

    // Obtenemos un Flux de PurchaseOrder sin importar la categoría, ya que asumimos que
    // ya se habrán filtrado y aquí solo entrarán las de Automotive.
    private static UnaryOperator<Flux<PurchaseOrder>> automotiveProcessing() {
        return flux -> flux
                .map(po -> new PurchaseOrder(po.item(), po.category(), po.price() + 100));
    }

    // Obtenemos un Flux de PurchaseOrder sin importar la categoría, ya que asumimos que
    // ya se habrán filtrado y aquí solo entrarán las de Kids.
    private static UnaryOperator<Flux<PurchaseOrder>> kidsProcessing() {
        return flux -> flux
                // Creamos una orden gratuita, pero empezamos (startWith) con la original.
                .flatMap(po -> getFreeKidsOrder(po).flux().startWith(po));
    }

    // En la vida real, esto podría ser una llamada de red o una operación de base de datos.
    private static Mono<PurchaseOrder> getFreeKidsOrder(PurchaseOrder order) {
        return Mono.fromSupplier(() -> new PurchaseOrder(
                order.item() + "-FREE",
                order.category(),
                0
        ));
    }

    public static Predicate<PurchaseOrder> canProcess() {
        return po -> PROCESSOR_MAP.containsKey(po.category());
    }

    public static UnaryOperator<Flux<PurchaseOrder>> getProcessor(String category) {
        return PROCESSOR_MAP.get(category);
    }
}
