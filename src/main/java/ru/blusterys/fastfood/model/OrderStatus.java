package ru.blusterys.fastfood.model;

public enum OrderStatus {
    NEW("Новый"),
    COOKING("Готовится"),
    DELIVERING("Доставляется"),
    DONE("Завершен"),
    CANCELED("Отменен");

    private final String title;

    OrderStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
