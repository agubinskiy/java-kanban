import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import enums.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldChangeEpicStatus() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0), 1);
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), 1);
        Subtask updatedSubtask1 = new Subtask(2, "Test Subtask2",
                "Test Subtask2 description",
                30, LocalDateTime.of(2025, 5, 1, 14, 0),
                1, Status.DONE);
        taskManager.createEpic(epic);

        assertEquals(epic.getStatus(), Status.NEW, "Некорректный статус эпика после создания");

        taskManager.createSubtask(subtask1);

        assertEquals(subtask1.getStatus(), Status.NEW, "Некорректный статус подзадачи после создания");
        assertEquals(epic.getStatus(), Status.NEW, "Некорректный статус эпика после добавления подзадачи");

        taskManager.updateSubtask(updatedSubtask1);

        assertEquals(epic.getStatus(), Status.DONE, "Некорректный статус эпика после обновления подзадачи");

        taskManager.createSubtask(subtask2);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Некорректный статус эпика после " +
                "добавления подзадачи");

        taskManager.removeSubtask(3);

        assertEquals(epic.getStatus(), Status.DONE, "Некорректный статус эпика после удаления подзадачи");

        taskManager.removeAllSubtasks();

        assertEquals(epic.getStatus(), Status.NEW, "Некорректный статус эпика после удаления всех подзадач");
    }

    @Test
    void TasksNotCrossTime() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task(1, "Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), Status.DONE);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertFalse(taskManager.isTasksCrossTime(task1, task2));
    }

    @Test
    void TasksCrossTime() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                90, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task(1, "Test addNewTask2", "Test addNewTask2 description",
                40, LocalDateTime.of(2025, 5, 1, 13, 0), Status.DONE);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertTrue(taskManager.isTasksCrossTime(task1, task2));
    }

    @Test
    void TasksHaveSameEndAndStartTime() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                60, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task(1, "Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), Status.DONE);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertFalse(taskManager.isTasksCrossTime(task1, task2));
    }

    @Test
    void oneTaskHasNoStartTime() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                60);
        Task task2 = new Task(1, "Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), Status.DONE);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertFalse(taskManager.isTasksCrossTime(task1, task2));
    }

    @Test
    void oneTaskHasNoDuration() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task(1, "Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), Status.DONE);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertFalse(taskManager.isTasksCrossTime(task1, task2));
    }


}
