package com.jmunoz.sec06.assignment;

public record Order(String item, String category, double price, int quantity) {

    public static Order of(String order) {
        String[] parts = order.split(":");
        return new Order(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]));
    }
}
