package ru.blusterys.fastfood.service;

import ru.blusterys.fastfood.model.CartItem;
import ru.blusterys.fastfood.model.Order;
import ru.blusterys.fastfood.model.OrderItem;
import ru.blusterys.fastfood.model.OrderStatus;
import ru.blusterys.fastfood.model.User;
import ru.blusterys.fastfood.storage.DataStore;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final DataStore dataStore;

    public OrderService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Order createOrder(User client, List<CartItem> cart) throws IOException {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Корзина пуста.");
        }

        List<OrderItem> items = new ArrayList<>();
        int total = 0;
        for (CartItem cartItem : cart) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getDish().getName(),
                    cartItem.getQuantity(),
                    cartItem.getDish().getPrice()
            );
            items.add(orderItem);
            total += orderItem.getTotalPrice();
        }

        Order order = new Order(dataStore.nextOrderId(), client.getId(), items, OrderStatus.NEW, total, LocalDateTime.now());
        List<Order> orders = dataStore.readOrders();
        orders.add(order);
        dataStore.saveOrders(orders);
        return order;
    }

    public List<Order> getClientOrders(String clientId) throws IOException {
        List<Order> result = new ArrayList<>();
        List<Order> orders = dataStore.readOrders();
        for (Order order : orders) {
            if (order.getClientId().equals(clientId)) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> getAllOrders() throws IOException {
        return dataStore.readOrders();
    }

    public void changeStatus(String orderId, OrderStatus status) throws IOException {
        List<Order> orders = dataStore.readOrders();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getId().equals(orderId)) {
                Order updatedOrder = new Order(
                        order.getId(),
                        order.getClientId(),
                        order.getItems(),
                        status,
                        order.getTotalPrice(),
                        order.getCreatedAt()
                );
                orders.set(i, updatedOrder);
                dataStore.saveOrders(orders);
                return;
            }
        }
        throw new IllegalArgumentException("Заказ не найден.");
    }
}
