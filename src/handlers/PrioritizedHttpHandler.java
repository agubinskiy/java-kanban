package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHttpHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET_PRIORITIZEDTASKS:
                handleGetPrioritized(httpExchange);
                break;
            default:
                sendNotFound(httpExchange, "Не известный эндпоинт");
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] split = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (split.length == 2) {
                return Endpoint.GET_PRIORITIZEDTASKS;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetPrioritized(HttpExchange httpExchange) throws IOException {
        Set<Task> tasks = taskManager.getPrioritizedTasks();
        sendText(httpExchange, gson.toJson(tasks, new TaskListTypeToken().getType()));
    }
}
