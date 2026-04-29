package ru.blusterys.fastfood;

import ru.blusterys.fastfood.model.CartItem;
import ru.blusterys.fastfood.model.Dish;
import ru.blusterys.fastfood.model.Order;
import ru.blusterys.fastfood.model.OrderItem;
import ru.blusterys.fastfood.model.OrderStatus;
import ru.blusterys.fastfood.model.Role;
import ru.blusterys.fastfood.model.User;
import ru.blusterys.fastfood.service.AuthService;
import ru.blusterys.fastfood.service.MenuService;
import ru.blusterys.fastfood.service.OrderService;
import ru.blusterys.fastfood.storage.DataStore;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FastFoodApp {
    private final Scanner scanner = new Scanner(System.in);
    private final DataStore dataStore = new DataStore(Path.of("data"));
    private final AuthService authService = new AuthService(dataStore);
    private final MenuService menuService = new MenuService(dataStore);
    private final OrderService orderService = new OrderService(dataStore);

    public static void main(String[] args) {
        new FastFoodApp().run();
    }

    private void run() {
        try {
            dataStore.initialize();
            mainMenu();
        } catch (IOException exception) {
            System.out.println("Ошибка работы с файлами: " + exception.getMessage());
        }
    }

    private void mainMenu() throws IOException {
        while (true) {
            System.out.println();
            System.out.println("=== БыстраяЕда ===");
            System.out.println("1. Регистрация клиента");
            System.out.println("2. Вход");
            System.out.println("0. Выход");
            String choice = readLine("Выберите действие: ");

            switch (choice) {
                case "1":
                    register();
                    break;
                case "2":
                    login();
                    break;
                case "0":
                    System.out.println("До свидания!");
                    return;
                default:
                    System.out.println("Неизвестный пункт меню.");
                    break;
            }
        }
    }

    private void register() throws IOException {
        try {
            String name = readLine("Имя: ");
            String login = readLine("Логин: ");
            String password = readLine("Пароль: ");
            User user = authService.register(login, password, name);
            System.out.println("Регистрация успешна. Добро пожаловать, " + user.getName() + "!");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void login() throws IOException {
        String login = readLine("Логин: ");
        String password = readLine("Пароль: ");
        User user = authService.login(login, password);
        if (user == null) {
            System.out.println("Неверный логин или пароль.");
            return;
        }
        if (user.getRole() == Role.ADMIN) {
            adminMenu(user);
            return;
        }
        clientMenu(user);
    }

    private void clientMenu(User user) throws IOException {
        List<CartItem> cart = new ArrayList<>();
        while (true) {
            System.out.println();
            System.out.println("=== Клиент: " + user.getName() + " ===");
            System.out.println("1. Просмотр меню");
            System.out.println("2. Поиск блюд");
            System.out.println("3. Добавить блюдо в корзину");
            System.out.println("4. Корзина и оформление заказа");
            System.out.println("5. История заказов");
            System.out.println("0. Выйти из аккаунта");
            String choice = readLine("Выберите действие: ");

            switch (choice) {
                case "1":
                    printDishes(menuService.getAvailableDishes());
                    break;
                case "2":
                    searchDishes();
                    break;
                case "3":
                    addToCart(cart);
                    break;
                case "4":
                    checkout(user, cart);
                    break;
                case "5":
                    printOrders(orderService.getClientOrders(user.getId()));
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Неизвестный пункт меню.");
                    break;
            }
        }
    }

    private void adminMenu(User user) throws IOException {
        while (true) {
            System.out.println();
            System.out.println("=== Администратор: " + user.getName() + " ===");
            System.out.println("1. Просмотр заказов");
            System.out.println("2. Изменить статус заказа");
            System.out.println("3. Просмотр блюд");
            System.out.println("4. Добавить блюдо");
            System.out.println("5. Редактировать блюдо");
            System.out.println("6. Удалить блюдо");
            System.out.println("0. Выйти из аккаунта");
            String choice = readLine("Выберите действие: ");

            switch (choice) {
                case "1":
                    printOrders(orderService.getAllOrders());
                    break;
                case "2":
                    changeOrderStatus();
                    break;
                case "3":
                    printDishes(menuService.getAllDishes());
                    break;
                case "4":
                    addDish();
                    break;
                case "5":
                    editDish();
                    break;
                case "6":
                    deleteDish();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Неизвестный пункт меню.");
                    break;
            }
        }
    }

    private void searchDishes() throws IOException {
        String query = readLine("Введите название или описание: ");
        printDishes(menuService.searchAvailableDishes(query));
    }

    private void addToCart(List<CartItem> cart) throws IOException {
        List<Dish> dishes = menuService.getAvailableDishes();
        Dish dish = selectDish(dishes);
        if (dish == null) {
            return;
        }
        int quantity = readPositiveInt("Количество: ");
        for (CartItem item : cart) {
            if (item.getDish().getId().equals(dish.getId())) {
                item.addQuantity(quantity);
                System.out.println("Количество обновлено.");
                return;
            }
        }
        cart.add(new CartItem(dish, quantity));
        System.out.println("Блюдо добавлено в корзину.");
    }

    private void checkout(User user, List<CartItem> cart) throws IOException {
        if (cart.isEmpty()) {
            System.out.println("Корзина пуста.");
            return;
        }
        printCart(cart);
        String confirm = readLine("Оформить заказ? (да/нет): ");
        if (!confirm.equalsIgnoreCase("да")) {
            return;
        }
        Order order = orderService.createOrder(user, cart);
        cart.clear();
        System.out.println("Заказ оформлен. Номер: " + order.getId());
    }

    private void changeOrderStatus() throws IOException {
        List<Order> orders = orderService.getAllOrders();
        Order order = selectOrder(orders);
        if (order == null) {
            return;
        }
        OrderStatus status = selectStatus();
        if (status == null) {
            return;
        }
        orderService.changeStatus(order.getId(), status);
        System.out.println("Статус заказа изменен.");
    }

    private void addDish() throws IOException {
        try {
            String name = readLine("Название: ");
            String description = readLine("Описание: ");
            int price = readPrice("Цена: ");
            boolean available = readBoolean("Доступно для заказа");
            menuService.addDish(name, description, price, available);
            System.out.println("Блюдо добавлено.");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void editDish() throws IOException {
        try {
            Dish dish = selectDish(menuService.getAllDishes());
            if (dish == null) {
                return;
            }
            String name = readLine("Новое название: ");
            String description = readLine("Новое описание: ");
            int price = readPrice("Новая цена: ");
            boolean available = readBoolean("Доступно для заказа");
            menuService.updateDish(dish.getId(), name, description, price, available);
            System.out.println("Блюдо обновлено.");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void deleteDish() throws IOException {
        try {
            Dish dish = selectDish(menuService.getAllDishes());
            if (dish == null) {
                return;
            }
            String confirm = readLine("Удалить блюдо \"" + dish.getName() + "\"? (да/нет): ");
            if (!confirm.equalsIgnoreCase("да")) {
                return;
            }
            menuService.deleteDish(dish.getId());
            System.out.println("Блюдо удалено.");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private Dish selectDish(List<Dish> dishes) {
        printDishes(dishes);
        if (dishes.isEmpty()) {
            return null;
        }
        int number = readInt("Введите номер блюда (0 - отмена): ");
        if (number == 0) {
            return null;
        }
        if (number < 1 || number > dishes.size()) {
            System.out.println("Некорректный номер.");
            return null;
        }
        return dishes.get(number - 1);
    }

    private Order selectOrder(List<Order> orders) {
        printOrders(orders);
        if (orders.isEmpty()) {
            return null;
        }
        int number = readInt("Введите номер заказа (0 - отмена): ");
        if (number == 0) {
            return null;
        }
        if (number < 1 || number > orders.size()) {
            System.out.println("Некорректный номер.");
            return null;
        }
        return orders.get(number - 1);
    }

    private OrderStatus selectStatus() {
        OrderStatus[] statuses = OrderStatus.values();
        for (int index = 0; index < statuses.length; index++) {
            System.out.println((index + 1) + ". " + statuses[index].getTitle());
        }
        int number = readInt("Введите новый статус (0 - отмена): ");
        if (number == 0) {
            return null;
        }
        if (number < 1 || number > statuses.length) {
            System.out.println("Некорректный номер.");
            return null;
        }
        return statuses[number - 1];
    }

    private void printDishes(List<Dish> dishes) {
        if (dishes.isEmpty()) {
            System.out.println("Блюда не найдены.");
            return;
        }
        for (int index = 0; index < dishes.size(); index++) {
            Dish dish = dishes.get(index);
            String availability = dish.isAvailable() ? "доступно" : "скрыто";
            System.out.println((index + 1) + ". " + dish.getName() + " - " + dish.getPrice() + " руб. [" + availability + "]");
            System.out.println("   " + dish.getDescription());
        }
    }

    private void printCart(List<CartItem> cart) {
        System.out.println("=== Корзина ===");
        int total = 0;
        for (CartItem item : cart) {
            System.out.println(item.getDish().getName() + " x " + item.getQuantity() + " = " + item.getTotalPrice() + " руб.");
            total += item.getTotalPrice();
        }
        System.out.println("Итого: " + total + " руб.");
    }

    private void printOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("Заказы не найдены.");
            return;
        }
        for (int index = 0; index < orders.size(); index++) {
            Order order = orders.get(index);
            System.out.println((index + 1)
                    + ". Заказ " + order.getId()
                    + " от " + order.getCreatedAt()
                    + ", статус: " + order.getStatus().getTitle()
                    + ", сумма: " + order.getTotalPrice() + " руб.");
            for (OrderItem item : order.getItems()) {
                System.out.println("   " + item.getDishName() + " x " + item.getQuantity() + " = " + item.getTotalPrice() + " руб.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Введите число больше нуля.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private int readPrice(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                int price = Integer.parseInt(value);
                if (price > 0) {
                    return price;
                }
                System.out.println("Цена должна быть больше нуля.");
            } catch (NumberFormatException exception) {
                System.out.println("Введите корректную цену.");
            }
        }
    }

    private boolean readBoolean(String prompt) {
        while (true) {
            String value = readLine(prompt + " (да/нет): ");
            if (value.equalsIgnoreCase("да")) {
                return true;
            }
            if (value.equalsIgnoreCase("нет")) {
                return false;
            }
            System.out.println("Введите \"да\" или \"нет\".");
        }
    }
}
