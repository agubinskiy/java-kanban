import enums.Status;
import exceptions.NotFoundException;
import exceptions.TasksCrossTimeException;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    //Добавление нового задания
    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    //Добавление нового эпика
    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test Epic", "Test Epic description");
        taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpic(1);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    //Добавление новой подзадачи
    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test Epic", "Test Epic description");
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", 30,
                LocalDateTime.of(2025, 5, 1, 12, 0), 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtask(2);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
        Assertions.assertEquals(subtasks, taskManager.getEpicSubtasks(1), "Список подзадач эпика некорректен");
    }

    //Подзадачу нельзя добавить в задачу или в подзадачу
    @Test
    void shouldNotAddSubtaskToTaskOrSubtask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Subtask subtask1 = new Subtask("Test Subtask", "Test Subtask description", 30,
                LocalDateTime.of(2025, 5, 1, 13, 0), 1);
        Subtask subtask2 = new Subtask("Test Subtask", "Test Subtask description", 30,
                LocalDateTime.of(2025, 5, 1, 14, 0), 2);
        taskManager.createTask(task);
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.createSubtask(subtask1),
                "Подзадача добавлена к задаче вместо эпика");
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtask(1),
                "Подзадача добавлена к задаче вместо эпика");
        Assertions.assertEquals(new ArrayList<Subtask>(), taskManager.getAllSubtasks());

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.createSubtask(subtask2),
                "Подзадача добавлена к задаче вместо эпика");
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtask(2),
                "Подзадача добавлена к задаче вместо эпика");
        Assertions.assertEquals(new ArrayList<Subtask>(), taskManager.getAllSubtasks());
    }

    //Обновление задачи
    @Test
    void updateTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task updatedTask = new Task(1, "Test updateTask", "Test updateTask description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), Status.IN_PROGRESS);
        taskManager.createTask(task);
        taskManager.updateTask(updatedTask);
        final Task savedTask = taskManager.getTask(1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(updatedTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(updatedTask, tasks.get(0), "Задачи не совпадают.");
    }

    //Обновление эпика
    @Test
    void updateEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Epic updatedEpic = new Epic(1, "Test updateEpic", "Test updateEpic description");
        taskManager.createEpic(epic);
        taskManager.updateEpic(updatedEpic);
        final Epic savedEpic = taskManager.getEpic(1);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(updatedEpic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(updatedEpic, epics.get(0), "Эпики не совпадают.");
    }

    //Обновление подзадачи
    @Test
    void updateSubtask() {
        Epic epic1 = new Epic("Test Epic1", "Test Epic1 description");
        Epic epic2 = new Epic("Test Epic2", "Test Epic2 description");
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", 30,
                LocalDateTime.of(2025, 5, 1, 12, 0), 1);
        Subtask updatedSubtask = new Subtask(3, "Test updatedSubtask",
                "Test updatedSubtask description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0),
                2, Status.NEW);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask);
        taskManager.updateSubtask(updatedSubtask);
        final Subtask savedSubtask = taskManager.getSubtask(3);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(updatedSubtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(updatedSubtask, subtasks.get(0), "Подзадачи не совпадают.");
        Assertions.assertEquals(subtasks, taskManager.getEpicSubtasks(2), "Список подзадач эпика некорректен");
        Assertions.assertNotEquals(subtasks, taskManager.getEpicSubtasks(1), "Список подзадач эпика некорректен");
    }

    @Test
    void removeTasks() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0));
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description",
                30, LocalDateTime.of(2025, 5, 1, 14, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.removeTask(2);

        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getTask(2),
                "Задача не удалена");
        Assertions.assertEquals(taskManager.getTask(3), tasks.get(1), "Задачи не совпадают.");

        taskManager.removeAllTasks();

        Assertions.assertEquals(taskManager.getAllTasks(), new ArrayList<Task>(), "Задачи не удалены.");
    }

    @Test
    void removeEpics() {
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        Epic epic2 = new Epic("Test addNewEpic2", "Test addNewEpic2 description");
        Epic epic3 = new Epic("Test addNewEpic3", "Test addNewEpic3 description");
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0), 1);
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), 2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.removeEpic(2);

        final List<Epic> epics = taskManager.getAllEpics();
        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getEpic(2), "Эпик не удалён");
        Assertions.assertEquals(taskManager.getEpic(3), epics.get(1), "Эпики не совпадают.");
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtask(5),
                "Подзадача не удалена");

        taskManager.removeAllEpics();

        Assertions.assertEquals(taskManager.getAllEpics(), new ArrayList<Epic>(), "Эпики не удалены.");
        Assertions.assertEquals(taskManager.getAllSubtasks(), new ArrayList<Subtask>(), "Подзадачи не удалены.");
    }

    @Test
    void removeSubtasks() {
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        Epic epic2 = new Epic("Test addNewEpic2", "Test addNewEpic2 description");
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description",
                30, LocalDateTime.of(2025, 5, 1, 12, 0), 1);
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description",
                30, LocalDateTime.of(2025, 5, 1, 13, 0), 2);
        Subtask subtask3 = new Subtask("Test Subtask2", "Test Subtask2 description",
                30, LocalDateTime.of(2025, 5, 1, 14, 0), 2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.removeSubtask(3);

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        Assertions.assertEquals(taskManager.getEpicSubtasks(1), new ArrayList<Subtask>(),
                "Подзадача не удалена");
        assertEquals(epic1.getChildTasksIds(), new ArrayList<Integer>(), "Подзадача не удалена");

        taskManager.removeAllSubtasks();

        Assertions.assertEquals(taskManager.getAllSubtasks(), new ArrayList<Subtask>(), "Подзадачи не удалены.");
        Assertions.assertEquals(taskManager.getEpicSubtasks(2), new ArrayList<Subtask>(), "Подзадачи не удалены");
    }

    @Test
    void shouldThrowCrossTimeException() {
        assertThrows(TasksCrossTimeException.class, () -> {
            Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description",
                    100, LocalDateTime.of(2025, 5, 1, 12, 0));
            Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description",
                    30, LocalDateTime.of(2025, 5, 1, 13, 0));

            taskManager.createTask(task1);
            taskManager.createTask(task2);
        });
    }
}