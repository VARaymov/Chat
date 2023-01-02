package server;

import common.props.PropertyReader;
import server.service.UserService;

import javax.xml.stream.events.DTD;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static common.constants.MessageConstants.REGEX;
import static common.enums.Command.BROADCAST_MESSAGE;
import static common.enums.Command.LIST_USERS;
import static common.enums.Command.PRIVATE_MESSAGE;

public class Server {
    private final int port;
    private List<Handler> handlers;

    private UserService userService;

    public Server(UserService userService) {
        this.userService = userService;
        this.handlers = new ArrayList<>();
        port = PropertyReader.getInstance().getPort();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server start!");
            userService.start();
            while (true) {
                System.out.println("Waiting for connection......");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                Handler handler = new Handler(socket, this);
                handler.handle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void broadcast(String from, String message) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("k:m d-MM-yyyy");
        String string = formatter.format(date);

        String msg = BROADCAST_MESSAGE.getCommand() + REGEX + String.format("%s [%s]: %s", string, from, message);
        for (Handler handler : handlers) {
            handler.send(msg);
        }
    }

    public void privateMessage(String from, String to, String message) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("k:m d-MM-yyyy");
        String string = formatter.format(date);

        String msg = PRIVATE_MESSAGE.getCommand() + REGEX + String.format("[%s][to %s]: %s", from, to, message);
        for (Handler handler : handlers) {
            if (handler.getUser().equals(from) || handler.getUser().equals(to)) {
                handler.send(msg);
            }
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public synchronized boolean isUserAlreadyOnline(String nick) {
        for (Handler handler : handlers) {
            if (handler.getUser().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void addHandler(Handler handler) {
        this.handlers.add(handler);
        sendContacts();
    }

    public synchronized void removeHandler(Handler handler) {
        this.handlers.remove(handler);
        sendContacts();
    }

    public synchronized void updateHandlerUsername() {
        sendContacts();
    }

    private void shutdown() {
        userService.stop();
    }

    private void sendContacts() {
        String contacts = handlers.stream()
                .map(Handler::getUser)
                .collect(Collectors.joining(REGEX));
        String msg = LIST_USERS.getCommand() + REGEX + contacts;

        for (Handler handler : handlers) {
            handler.send(msg);
        }
    }
}
