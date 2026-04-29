package ru.blusterys.fastfood.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String id;
    private final String clientId;
    private final List<OrderItem> items;
    private final OrderStatus status;
    private final int totalPrice;
    private final LocalDateTime createdAt;

    public Order(
            String id,
            String clientId,
            List<OrderItem> items,
            OrderStatus status,
            int totalPrice,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
