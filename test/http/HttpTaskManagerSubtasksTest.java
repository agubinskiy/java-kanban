package http;

import api.HttpTaskServer;
import com.google.gson.Gson;
import enums.Status;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubtasksTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson;

    public HttpTaskManagerSubtasksTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = httpTaskServer.getGson();
        httpTaskServer.start();

        Epic epic = new Epic("Test 1", "Testing epic 1");
        taskManager.createEpic(epic);
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1", 30,
                LocalDateTime.now(), 1);
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1", "Testing subtask 1", 30,
                LocalDateTime.of(2025, 5, 18, 14, 0), 1);
        Subtask subtask2 = new Subtask(2, "Test 2", "Testing subtask 1", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0), 1, Status.NEW);
        taskManager.createSubtask(subtask1);
        String taskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1", 30, LocalDateTime.now(), 1);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task taskFromManager = taskManager.getAllSubtasks().getFirst();

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(gson.toJson(taskFromManager), response.body(), "Некорректные данные");
    }

    @Test
    void testGetSubtasks() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1", "Testing subtask 1", 30,
                LocalDateTime.of(2025, 5, 18, 14, 0), 1);
        Subtask subtask2 = new Subtask("Test 2", "Testing subtask 2", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0), 1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(gson.toJson(tasksFromManager), response.body(), "Некорректные данные");
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1",
                30, LocalDateTime.now(), 1);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testDeleteSubtasks() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1", "Testing subtask 1", 30,
                LocalDateTime.of(2025, 5, 18, 14, 0), 1);
        Subtask subtask2 = new Subtask("Test 2", "Testing subtask 2", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0), 1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testUnknownEndpoint() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1", 30,
                LocalDateTime.now(), 1);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1/name");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testGetIncorrectSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1", 30,
                LocalDateTime.now(), 1);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteIncorrectSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1", 30, LocalDateTime.now(), 1);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testUpdateIncorrectSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1", "Testing subtask 1", 30,
                LocalDateTime.of(2025, 5, 18, 14, 0), 1);
        Subtask subtask2 = new Subtask(3, "Test 2", "Testing subtask 1", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0), 1, Status.NEW);
        taskManager.createSubtask(subtask1);
        String taskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void testCreateCrossedTimeSubtask() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("Test 1", "Testing subtask 1", 90,
                LocalDateTime.of(2025, 5, 18, 14, 0), 1);
        Subtask subtask2 = new Subtask("Test 2", "Testing subtask 1", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0), 1);
        taskManager.createSubtask(subtask1);
        String taskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}
