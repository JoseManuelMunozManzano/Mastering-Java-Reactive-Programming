package com.jmunoz.sec10.assignment.groupby;

import com.jmunoz.common.Util;

public record PurchaseOrder(String item, String category, Integer price) {

    public static PurchaseOrder create() {
        var commerce = Util.faker().commerce();
        return new PurchaseOrder(
                commerce.productName(),
                commerce.department(),
                Util.faker().random().nextInt(10, 100)
        );
    }

    public static PurchaseOrder increasePrice(PurchaseOrder order) {
        return new PurchaseOrder(
                order.item(),
                order.category(),
                order.price() + 100
        );
    }

    public static PurchaseOrder freeOrder(PurchaseOrder order) {
        return new PurchaseOrder(
                order.item(),
                order.category(),
                0
        );
    }
}
