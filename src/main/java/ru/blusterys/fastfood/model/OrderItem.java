package ru.blusterys.fastfood.model;

public class OrderItem {
    private final String dishName;
    private final int quantity;
    private final int price;

    public OrderItem(String dishName, int quantity, int price) {
        this.dishName = dishName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getDishName() {
        return dishName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public int getTotalPrice() {
        return price * quantity;
    }
}
