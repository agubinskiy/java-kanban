package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import exceptions.NotFoundException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHttpHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHttpHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, method);

        switch (endpoint) {
            case GET_EPIC:
                handleGetEpic(httpExchange);
                break;
            case GET_EPICS:
                handleGetEpics(httpExchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(httpExchange);
                break;
            case POST_EPIC:
                handlePostEpic(httpExchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(httpExchange);
                break;
            case DELETE_EPICS:
                handleDeleteEpics(httpExchange);
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
                    return Endpoint.GET_EPICS;
                } else if (split.length == 3) {
                    return Endpoint.GET_EPIC;
                } else if ((split.length == 4) && (split[3].equals("subtasks"))) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                }
            }
            case ("POST") -> {
                return Endpoint.POST_EPIC;
            }
            case "DELETE" -> {
                if (split.length == 2) {
                    return Endpoint.DELETE_EPICS;
                } else if (split.length == 3) {
                    return Endpoint.DELETE_EPIC;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetEpic(HttpExchange httpExchange) throws IOException {
        if (getTaskIdFromRequest(httpExchange).isEmpty()) {
            sendIncorrectRequest(httpExchange, "Некорректный идентификатор эпика");
            return;
        }
        int id = getTaskIdFromRequest(httpExchange).get();
        try {
            Epic epic = taskManager.getEpic(id);
            sendText(httpExchange, gson.toJson(epic));
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendText(httpExchange, gson.toJson(epics, new TaskListTypeToken().getType()));
    }

    private void handleGetEpicSubtasks(HttpExchange httpExchange) throws IOException {
        if (getTaskIdFromRequest(httpExchange).isEmpty()) {
            sendIncorrectRequest(httpExchange, "Некорректный идентификатор эпика");
            return;
        }
        int id = getTaskIdFromRequest(httpExchange).get();
        try {
            List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
            sendText(httpExchange, gson.toJson(subtasks, new TaskListTypeToken().getType()));
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handlePostEpic(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        try {
            Epic epic = gson.fromJson(requestBody, Epic.class);
            if (epic.getTaskId() == 0) {
                taskManager.createEpic(epic);
                sendTextCreate(httpExchange, "Эпик успешно создан");
                System.out.println(epic);
            } else {
                try {
                    taskManager.updateEpic(epic);
                    sendTextCreate(httpExchange, "Эпик успешно обновлён`");
                } catch (NotFoundException e) {
                    sendIncorrectRequest(httpExchange, e.getMessage());
                }
            }
        } catch (Exception e) {
            sendIncorrectRequest(httpExchange, "Некорректное тело запроса");
        }
    }

    private void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        if (getTaskIdFromRequest(httpExchange).isEmpty()) {
            sendIncorrectRequest(httpExchange, "Некорректный идентификатор эпика");
            return;
        }
        int id = getTaskIdFromRequest(httpExchange).get();
        try {
            taskManager.removeEpic(id);
            sendText(httpExchange, "Эпик с id " + id + " успешно удалён");
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        }
    }

    private void handleDeleteEpics(HttpExchange httpExchange) throws IOException {
        taskManager.removeAllEpics();
        sendText(httpExchange, "Эпики удалены");
    }
}
