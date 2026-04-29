package ru.blusterys.fastfood.service;

import ru.blusterys.fastfood.model.Dish;
import ru.blusterys.fastfood.storage.DataStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private final DataStore dataStore;

    public MenuService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<Dish> getAllDishes() throws IOException {
        return dataStore.readDishes();
    }

    public List<Dish> getAvailableDishes() throws IOException {
        List<Dish> result = new ArrayList<>();
        List<Dish> dishes = dataStore.readDishes();
        for (Dish dish : dishes) {
            if (dish.isAvailable()) {
                result.add(dish);
            }
        }
        return result;
    }

    public List<Dish> searchAvailableDishes(String query) throws IOException {
        List<Dish> result = new ArrayList<>();
        String text = query.toLowerCase();
        List<Dish> dishes = getAvailableDishes();
        for (Dish dish : dishes) {
            String name = dish.getName().toLowerCase();
            String description = dish.getDescription().toLowerCase();
            if (name.contains(text) || description.contains(text)) {
                result.add(dish);
            }
        }
        return result;
    }

    public Dish addDish(String name, String description, int price, boolean available) throws IOException {
        validateDish(name, description, price);
        List<Dish> dishes = dataStore.readDishes();
        Dish dish = new Dish(dataStore.nextDishId(), name, description, price, available);
        dishes.add(dish);
        dataStore.saveDishes(dishes);
        return dish;
    }

    public void updateDish(String id, String name, String description, int price, boolean available) throws IOException {
        validateDish(name, description, price);
        List<Dish> dishes = dataStore.readDishes();
        for (int i = 0; i < dishes.size(); i++) {
            Dish dish = dishes.get(i);
            if (dish.getId().equals(id)) {
                dishes.set(i, new Dish(id, name, description, price, available));
                dataStore.saveDishes(dishes);
                return;
            }
        }
        throw new IllegalArgumentException("Блюдо не найдено.");
    }

    public void deleteDish(String id) throws IOException {
        List<Dish> dishes = dataStore.readDishes();
        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getId().equals(id)) {
                dishes.remove(i);
                dataStore.saveDishes(dishes);
                return;
            }
        }
        throw new IllegalArgumentException("Блюдо не найдено.");
    }

    private void validateDish(String name, String description, int price) {
        if (name.trim().isEmpty() || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Название и описание обязательны.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Цена должна быть больше нуля.");
        }
    }
}
