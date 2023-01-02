package server.service;

public interface UserService {
    void start();

    void stop();

    String authenticate(String login, String password);

    String changeNick(String oldNick, String newNick);

    void changePassword(String login, String oldPassword, String newPassword);
}
