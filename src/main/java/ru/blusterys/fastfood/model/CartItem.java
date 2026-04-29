package ru.blusterys.fastfood.model;

public class CartItem {
    private final Dish dish;
    private int quantity;

    public CartItem(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int count) {
        quantity += count;
    }

    public int getTotalPrice() {
        return dish.getPrice() * quantity;
    }
}
