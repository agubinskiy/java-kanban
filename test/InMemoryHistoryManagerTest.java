import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void emptyHistory() {
        List<Task> emptyList = new ArrayList<>();
        Assertions.assertEquals(emptyList, taskManager.getHistory(), "История просмотра некорректна");
    }

    @Test
    void doubleGettingTask() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);

        taskManager.createTask(task1);
        taskManager.getTask(1);
        taskManager.getTask(1);

        Assertions.assertEquals(tasks, taskManager.getHistory(), "История просмотра некорректна");
    }

    @Test
    void shouldShowOldTasksVersion() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task(1, "Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), Status.DONE);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);

        taskManager.createTask(task1);
        taskManager.getTask(1);
        taskManager.updateTask(task2);

        Assertions.assertEquals(tasks, taskManager.getHistory(), "История просмотра некорректна");
    }

    @Test
    void shouldShowTasksInHistoryAfterRewriting() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        final List<Task> task1Task2 = List.of(task1, task2);
        final List<Task> task2Task1 = List.of(task2, task1);

        taskManager.getTask(1);
        taskManager.getTask(2);

        List<Task> history = taskManager.getHistory();

        //Проверяем корректность истории
        assertNotNull(history, "История пуста");
        assertEquals(history, task1Task2, "История некорректна");

        //Проверяем корректность истории после перезаписи последней записи
        taskManager.getTask(2);
        history = taskManager.getHistory();
        assertEquals(history, task1Task2, "История некорректна");

        //Проверяем корректность истории после перезаписи первой записи
        taskManager.getTask(1);
        history = taskManager.getHistory();
        assertEquals(history, task2Task1, "История некорректна");
    }

    @Test
    void shouldRemoveTaskFromHistoryAfterRemovingTask() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        final List<Task> listOfTask1 = List.of(task1);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.removeTask(2);
        List<Task> history = taskManager.getHistory();

        //Проверяем корректность истории
        assertNotNull(history, "История пуста");
        assertEquals(history, listOfTask1, "История некорректна");
    }

    @Test
    void shouldRemoveEpicFromHistoryAfterRemovingEpic() {
        Task task = new Task("Test addNewTask1", "Test addNewTask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), 2);
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        final List<Task> listOfTask = List.of(task);
        final List<Task> listOfAllTasks = List.of(task, epic, subtask);

        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
        List<Task> history = taskManager.getHistory();

        //Проверяем корректность истории
        assertNotNull(history, "История пуста");
        assertEquals(history, listOfAllTasks, "История некорректна");

        //Проверяем корректность истории после удаления эпика
        taskManager.removeEpic(2);
        history = taskManager.getHistory();
        assertNotNull(history, "История пуста");
        assertEquals(history, listOfTask, "История некорректна");
    }

    @Test
    void shouldRemoveTaskFromHistoryAfterRemovingMiddleTask() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0));
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description",
                30, LocalDateTime.of(2025, 5, 1, 14, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        final List<Task> task1Task3 = List.of(task1, task3);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(3);
        taskManager.removeTask(2);
        List<Task> history = taskManager.getHistory();

        //Проверяем корректность истории
        assertNotNull(history, "История пуста");
        assertEquals(history, task1Task3, "История некорректна");
    }
}
