package server.service.impl;

import server.error.NickAlreadyIsBusyException;
import server.error.UserNotFoundException;
import server.error.WrongCredentialsException;
import server.model.User;
import server.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class InMemoryUserServiceImpl implements UserService {

    private List<User> users;

    public InMemoryUserServiceImpl() {
        this.users = new ArrayList<>();
    }

    @Override
    public void start() {
        users.addAll(List.of(
                new User("log1", "pass1", "nick1"),
                new User("log2", "pass2", "nick2"),
                new User("log3", "pass3", "nick3"),
                new User("log4", "pass4", "nick4"),
                new User("log5", "pass5", "nick5")
        ));
        System.out.println("User service started");
    }

    @Override
    public void stop() {
        System.out.println("User service stopped");
    }

    @Override
    public String authenticate(String login, String password) {
        System.out.println("Auth log: " + login + " pass: " + password);
        for (User user : users) {
            if (login.equals(user.getLogin()) && password.equals(user.getPassword())) {
                return user.getNick();
            }
        }

        throw new WrongCredentialsException("Wrong login or password");
    }


    private User findUserByNickname(String nickname) {
        for (User user : users) {
            if (user.getNick().equals(nickname)) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }

    private User findUserByLogin(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }

    private User findUserByNick(String nick) {
        for (User user : users) {
            if (user.getNick().equals(nick)) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }

    private boolean isNickBusy(String newNick) {
        for (User user : users) {
            if (user.getNick().equals(newNick)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void changePassword(String nick, String oldPassword, String newPassword) {
        User userByLogin = findUserByNick(nick);
        if (userByLogin.getPassword().equals(oldPassword)) {
            userByLogin.setPassword(newPassword);
        } else {
            System.out.println("Некорректный пароль");
        }
    }

    @Override
    public String changeNick(String oldNick, String newNick) {
        if (isNickBusy(newNick)) {
            System.out.println("Ник уже занят");
        }
        User user = findUserByNickname(oldNick);
        user.setNick(newNick);
        return newNick;
    }
}
