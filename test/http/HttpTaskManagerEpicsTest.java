package http;

import api.HttpTaskServer;
import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerEpicsTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson;

    public HttpTaskManagerEpicsTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = httpTaskServer.getGson();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1");
        Epic epic2 = new Epic(1, "Test 2", "Testing epic 1");
        taskManager.createEpic(epic1);
        String taskJson = gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic taskFromManager = taskManager.getAllEpics().getFirst();

        assertNotNull(taskFromManager, "Эпики не возвращаются");
        assertEquals(gson.toJson(taskFromManager), response.body(), "Некорректные данные");
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1");
        Epic epic2 = new Epic("Test 2", "Testing epic 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(gson.toJson(tasksFromManager), response.body(), "Некорректные данные");
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing task 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void testDeleteEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing epic 1");
        Epic epic2 = new Epic("Test 2", "Testing epic 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(0, tasksFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    void testUnknownEndpoint() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/name");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testGetIncorrectEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testDeleteIncorrectEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testUpdateIncorrectEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test 1", "Testing task 1");
        Epic epic2 = new Epic(2, "Test 2", "Testing task 1");
        taskManager.createEpic(epic1);
        String taskJson = gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1", "Testing epic 1");
        Subtask subtask = new Subtask("Test 2", "Testing epic 1", 30, LocalDateTime.now(),
                1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(1);

        assertNotNull(epicSubtasks, "Эпики не возвращаются");
        assertEquals(gson.toJson(epicSubtasks), response.body(), "Некорректные данные");
    }
}
