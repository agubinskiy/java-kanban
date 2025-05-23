package http;

import api.HttpTaskServer;
import com.google.gson.Gson;
import enums.Status;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class HttpTaskManagerTasksTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson;

    public HttpTaskManagerTasksTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.taskManager = new InMemoryTaskManager();
        taskManager = httpTaskServer.getTaskManager();
        gson = httpTaskServer.getGson();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 30, LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 30, LocalDateTime.now());
        Task task2 = new Task(1, "Test 2", "Testing task 1", 30,
                LocalDateTime.now(), Status.NEW);
        taskManager.createTask(task1);
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 30, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task taskFromManager = taskManager.getAllTasks().getFirst();

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(gson.toJson(taskFromManager), response.body(), "Некорректные данные");
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 30,
                LocalDateTime.of(2025, 5, 18, 14, 0));
        Task task2 = new Task("Test 2", "Testing task 2", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(gson.toJson(tasksFromManager), response.body(), "Некорректные данные");
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 30, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testDeleteTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 30,
                LocalDateTime.of(2025, 5, 18, 14, 0));
        Task task2 = new Task("Test 2", "Testing task 2", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testUnknownEndpoint() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 30, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1/name");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testGetIncorrectTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 30, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteIncorrectTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 30, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testUpdateIncorrectTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 30,
                LocalDateTime.of(2025, 5, 18, 14, 0));
        Task task2 = new Task(2, "Test 2", "Testing task 1", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0), Status.NEW);
        taskManager.createTask(task1);
        String taskJson = gson.toJson(task2);

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
    void testCreateCrossedTimeTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 90,
                LocalDateTime.of(2025, 5, 18, 14, 0));
        Task task2 = new Task("Test 2", "Testing task 1", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0));
        taskManager.createTask(task1);
        String taskJson = gson.toJson(task2);

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
