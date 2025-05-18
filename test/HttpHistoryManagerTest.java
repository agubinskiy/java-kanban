import api.HttpTaskServer;
import com.google.gson.Gson;
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

public class HttpHistoryManagerTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    Gson gson;

    public HttpHistoryManagerTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        httpTaskServer = new HttpTaskServer();
        taskManager = httpTaskServer.getTaskManager();
        gson = httpTaskServer.getGson();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 30,
                LocalDateTime.of(2025, 5, 18, 14, 0));
        Task task2 = new Task("Test 2", "Testing task 2", 30,
                LocalDateTime.of(2025, 5, 18, 15, 0));
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1", 30,
                LocalDateTime.of(2025, 5, 18, 16, 0), 3);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        taskManager.getTask(2);
        taskManager.getSubtask(4);
        taskManager.getEpic(3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = List.of(taskManager.getTask(2), taskManager.getSubtask(4),
                taskManager.getEpic(3));

        assertEquals(response.body(), gson.toJson(tasksFromManager), "Некорректные данные");
    }
}
