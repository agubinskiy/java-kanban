package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exceptions.NotFoundException;
import exceptions.TasksCrossTimeException;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHttpHandler extends BaseHttpHandler implements HttpHandler {
    public SubtaskHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET_SUBTASK:
                handleGetSubtask(httpExchange);
                break;
            case GET_SUBTASKS:
                handleGetSubtasks(httpExchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(httpExchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(httpExchange);
                break;
            case DELETE_SUBTASKS:
                handleDeleteSubtasks(httpExchange);
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
                    return Endpoint.GET_SUBTASKS;
                } else if (split.length == 3) {
                    return Endpoint.GET_SUBTASK;
                }
            }
            case ("POST") -> {
                return Endpoint.POST_SUBTASK;
            }
            case "DELETE" -> {
                if (split.length == 2) {
                    return Endpoint.DELETE_SUBTASKS;
                } else if (split.length == 3) {
                    return Endpoint.DELETE_SUBTASK;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetSubtask(HttpExchange httpExchange) throws IOException {
        if (getTaskIdFromRequest(httpExchange).isEmpty()) {
            sendIncorrectRequest(httpExchange, "Некорректный идентификатор подзадачи");
            return;
        }
        int id = getTaskIdFromRequest(httpExchange).get();
        try {
            Subtask subtask = taskManager.getSubtask(id);
            sendText(httpExchange, gson.toJson(subtask));
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendText(httpExchange, gson.toJson(subtasks, new TaskListTypeToken().getType()));
    }

    private void handlePostSubtask(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        try {
            Subtask subtask = gson.fromJson(requestBody, Subtask.class);
            if (subtask.getTaskId() == 0) {
                try {
                    taskManager.createSubtask(subtask);
                    sendTextCreate(httpExchange, "Подзадача успешно создана");
                } catch (TasksCrossTimeException e) {
                    sendHasInteractions(httpExchange, e.getMessage());
                }
            } else {
                try {
                    taskManager.updateSubtask(subtask);
                    sendTextCreate(httpExchange, "Подзадача успешно обновлена");
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

    private void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        if (getTaskIdFromRequest(httpExchange).isEmpty()) {
            sendIncorrectRequest(httpExchange, "Некорректный идентификатор подзадачи");
            return;
        }
        int id = getTaskIdFromRequest(httpExchange).get();
        try {
            taskManager.removeSubtask(id);
            sendText(httpExchange, "Подзадача с id " + id + " успешно удалена");
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleDeleteSubtasks(HttpExchange httpExchange) throws IOException {
        taskManager.removeAllSubtasks();
        sendText(httpExchange, "Подзадачи удалены");
    }
}