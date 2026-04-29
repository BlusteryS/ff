package ru.blusterys.fastfood.model;

public class Dish {
    private final String id;
    private final String name;
    private final String description;
    private final int price;
    private final boolean available;

    public Dish(String id, String name, String description, int price, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }
}
