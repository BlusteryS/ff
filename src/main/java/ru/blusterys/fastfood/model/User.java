package ru.blusterys.fastfood.model;

public class User {
    private final String id;
    private final String login;
    private final String password;
    private final Role role;
    private final String name;

    public User(String id, String login, String password, Role role, String name) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }
}
