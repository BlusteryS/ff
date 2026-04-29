package ru.blusterys.fastfood.service;

import ru.blusterys.fastfood.model.Role;
import ru.blusterys.fastfood.model.User;
import ru.blusterys.fastfood.storage.DataStore;

import java.io.IOException;
import java.util.List;

public class AuthService {
    private final DataStore dataStore;

    public AuthService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public User register(String login, String password, String name) throws IOException {
        if (login.trim().isEmpty() || password.trim().isEmpty() || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Все поля обязательны.");
        }

        List<User> users = dataStore.readUsers();
        for (User user : users) {
            if (user.getLogin().equalsIgnoreCase(login)) {
                throw new IllegalArgumentException("Пользователь с таким логином уже существует.");
            }
        }

        User user = new User(dataStore.nextUserId(), login, password, Role.CLIENT, name);
        users.add(user);
        dataStore.saveUsers(users);
        return user;
    }

    public User login(String login, String password) throws IOException {
        List<User> users = dataStore.readUsers();
        for (User user : users) {
            if (user.getLogin().equalsIgnoreCase(login) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
