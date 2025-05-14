package api;

import com.sun.net.httpserver.HttpServer;
import handlers.TaskHttpHandler;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();

        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHttpHandler(taskManager));
        httpServer.start();
    }
}
