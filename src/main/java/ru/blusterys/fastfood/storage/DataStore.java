package ru.blusterys.fastfood.storage;

import ru.blusterys.fastfood.model.Dish;
import ru.blusterys.fastfood.model.Order;
import ru.blusterys.fastfood.model.OrderItem;
import ru.blusterys.fastfood.model.OrderStatus;
import ru.blusterys.fastfood.model.Role;
import ru.blusterys.fastfood.model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static final String SEPARATOR = "|";

    private final Path dataDirectory;
    private final Path usersFile;
    private final Path dishesFile;
    private final Path ordersFile;

    public DataStore(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        usersFile = dataDirectory.resolve("users.txt");
        dishesFile = dataDirectory.resolve("dishes.txt");
        ordersFile = dataDirectory.resolve("orders.txt");
    }

    public void initialize() throws IOException {
        Files.createDirectories(dataDirectory);
        createFile(usersFile);
        createFile(dishesFile);
        createFile(ordersFile);
    }

    public List<User> readUsers() throws IOException {
        List<User> users = new ArrayList<>();
        List<String> lines = Files.readAllLines(usersFile, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = split(line, 5);
            users.add(new User(parts[0], parts[1], parts[2], Role.valueOf(parts[3]), parts[4]));
        }
        return users;
    }

    public void saveUsers(List<User> users) throws IOException {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.add(user.getId()
                    + SEPARATOR + clean(user.getLogin())
                    + SEPARATOR + clean(user.getPassword())
                    + SEPARATOR + user.getRole().name()
                    + SEPARATOR + clean(user.getName()));
        }
        Files.write(usersFile, lines, StandardCharsets.UTF_8);
    }

    public List<Dish> readDishes() throws IOException {
        List<Dish> dishes = new ArrayList<>();
        List<String> lines = Files.readAllLines(dishesFile, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = split(line, 5);
            dishes.add(new Dish(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), Boolean.parseBoolean(parts[4])));
        }
        return dishes;
    }

    public void saveDishes(List<Dish> dishes) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Dish dish : dishes) {
            lines.add(dish.getId()
                    + SEPARATOR + clean(dish.getName())
                    + SEPARATOR + clean(dish.getDescription())
                    + SEPARATOR + dish.getPrice()
                    + SEPARATOR + dish.isAvailable());
        }
        Files.write(dishesFile, lines, StandardCharsets.UTF_8);
    }

    public List<Order> readOrders() throws IOException {
        List<Order> orders = new ArrayList<>();
        List<String> lines = Files.readAllLines(ordersFile, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = split(line, 6);
            orders.add(new Order(
                    parts[0],
                    parts[1],
                    parseOrderItems(parts[5]),
                    OrderStatus.valueOf(parts[2]),
                    Integer.parseInt(parts[4]),
                    LocalDateTime.parse(parts[3])
            ));
        }
        return orders;
    }

    public void saveOrders(List<Order> orders) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Order order : orders) {
            lines.add(order.getId()
                    + SEPARATOR + order.getClientId()
                    + SEPARATOR + order.getStatus().name()
                    + SEPARATOR + order.getCreatedAt()
                    + SEPARATOR + order.getTotalPrice()
                    + SEPARATOR + formatOrderItems(order.getItems()));
        }
        Files.write(ordersFile, lines, StandardCharsets.UTF_8);
    }

    public String nextUserId() throws IOException {
        return nextIdFromUsers(readUsers());
    }

    public String nextDishId() throws IOException {
        return nextIdFromDishes(readDishes());
    }

    public String nextOrderId() throws IOException {
        return nextIdFromOrders(readOrders());
    }

    private void createFile(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
    }

    private String[] split(String line, int expectedParts) throws IOException {
        String[] parts = line.split("\\|", -1);
        if (parts.length != expectedParts) {
            throw new IOException("Повреждена строка файла данных: " + line);
        }
        return parts;
    }

    private String clean(String value) {
        return value.replace('|', '/').replace('\n', ' ').replace('\r', ' ');
    }

    private List<OrderItem> parseOrderItems(String value) {
        List<OrderItem> items = new ArrayList<>();
        if (value.trim().isEmpty()) {
            return items;
        }
        String[] itemLines = value.split(",");
        for (String itemLine : itemLines) {
            String[] parts = itemLine.split(":");
            items.add(new OrderItem(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
        }
        return items;
    }

    private String formatOrderItems(List<OrderItem> items) {
        String result = "";
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            if (i > 0) {
                result += ",";
            }
            result += clean(item.getDishName()).replace(':', '/').replace(',', '/')
                    + ":" + item.getQuantity() + ":" + item.getPrice();
        }
        return result;
    }

    private String nextIdFromUsers(List<User> users) {
        int maxId = 0;
        for (User user : users) {
            int id = Integer.parseInt(user.getId());
            if (id > maxId) {
                maxId = id;
            }
        }
        return String.valueOf(maxId + 1);
    }

    private String nextIdFromDishes(List<Dish> dishes) {
        int maxId = 0;
        for (Dish dish : dishes) {
            int id = Integer.parseInt(dish.getId());
            if (id > maxId) {
                maxId = id;
            }
        }
        return String.valueOf(maxId + 1);
    }

    private String nextIdFromOrders(List<Order> orders) {
        int maxId = 0;
        for (Order order : orders) {
            int id = Integer.parseInt(order.getId());
            if (id > maxId) {
                maxId = id;
            }
        }
        return String.valueOf(maxId + 1);
    }
}
