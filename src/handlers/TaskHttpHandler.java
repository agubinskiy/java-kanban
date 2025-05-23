package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exceptions.NotFoundException;
import exceptions.TasksCrossTimeException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHttpHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET_TASK:
                handleGetTask(httpExchange);
                break;
            case GET_TASKS:
                handleGetTasks(httpExchange);
                break;
            case POST_TASK:
                handlePostTask(httpExchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(httpExchange);
                break;
            case DELETE_TASKS:
                handleDeleteTasks(httpExchange);
                break;
            default:
                sendNotFound(httpExchange, "Не известный эндпоинт");
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] split = requestPath.split("/");

        switch (requestMethod) {
            case "GET" -> {
                if (split.length == 2) {
                    return Endpoint.GET_TASKS;
                } else if (split.length == 3) {
                    return Endpoint.GET_TASK;
                }
            }
            case ("POST") -> {
                return Endpoint.POST_TASK;
            }
            case "DELETE" -> {
                if (split.length == 2) {
                    return Endpoint.DELETE_TASKS;
                } else if (split.length == 3) {
                    return Endpoint.DELETE_TASK;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetTask(HttpExchange httpExchange) throws IOException {
        if (getTaskIdFromRequest(httpExchange).isEmpty()) {
            sendIncorrectRequest(httpExchange, "Некорректный идентификатор задачи");
            return;
        }
        int id = getTaskIdFromRequest(httpExchange).get();
        try {
            Task task = taskManager.getTask(id);
            sendText(httpExchange, gson.toJson(task));
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(httpExchange, gson.toJson(tasks, new TaskListTypeToken().getType()));
    }

    private void handlePostTask(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        try {
            Task task = gson.fromJson(requestBody, Task.class);
            if (task.getTaskId() == 0) {
                try {
                    taskManager.createTask(task);
                    sendTextCreate(httpExchange, "Задача успешно создана");
                } catch (TasksCrossTimeException e) {
                    sendHasInteractions(httpExchange, e.getMessage());
                }
            } else {
                try {
                    taskManager.updateTask(task);
                    sendTextCreate(httpExchange, "Задача успешно обновлена");
                } catch (NotFoundException e) {
                    sendIncorrectRequest(httpExchange, e.getMessage());
                } catch (TasksCrossTimeException e) {
                    sendHasInteractions(httpExchange, e.getMessage());
                }
            }
        } catch (Exception e) {
            sendIncorrectRequest(httpExchange, "Некорректное тело запроса");
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        if (getTaskIdFromRequest(httpExchange).isEmpty()) {
            sendIncorrectRequest(httpExchange, "Некорректный идентификатор задачи");
            return;
        }
        int id = getTaskIdFromRequest(httpExchange).get();
        try {
            taskManager.removeTask(id);
            sendText(httpExchange, "Задача с id " + id + " успешно удалена");
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleDeleteTasks(HttpExchange httpExchange) throws IOException {
        taskManager.removeAllTasks();
        sendText(httpExchange, "Задачи удалены");
    }
}
