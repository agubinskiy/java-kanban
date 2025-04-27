import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        Task.setCounter(0);
        InMemoryTaskManager.tasks.clear();
        InMemoryTaskManager.epics.clear();
        InMemoryTaskManager.subtasks.clear();
    }

    //Добавление нового задания
    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
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
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", 1);
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
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Subtask subtask1 = new Subtask("Test Subtask", "Test Subtask description", 1);
        Subtask subtask2 = new Subtask("Test Subtask", "Test Subtask description", 2);
        taskManager.createTask(task);
        taskManager.createSubtask(subtask1);
        assertNull(taskManager.getSubtask(2));
        Assertions.assertEquals(new ArrayList<Subtask>(), taskManager.getAllSubtasks());

        taskManager.createSubtask(subtask2);

        assertNull(taskManager.getSubtask(2));
        Assertions.assertEquals(new ArrayList<Subtask>(), taskManager.getAllSubtasks());
    }

    //Обновление задачи
    @Test
    void updateTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Task updatedTask = new Task(1, "Test updateTask", "Test updateTask description", Status.IN_PROGRESS);
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
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", 1);
        Subtask updatedSubtask = new Subtask(3, "Test updatedSubtask",
                "Test updatedSubtask description", 2);
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
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description");
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description");
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.removeTask(2);

        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        Assertions.assertNull(taskManager.getTask(2), "Задача не удалена");
        Assertions.assertEquals(taskManager.getTask(3), tasks.get(1), "Задачи не совпадают.");

        taskManager.removeAllTasks();

        Assertions.assertEquals(taskManager.getAllTasks(), new ArrayList<Task>(), "Задачи не удалены.");
    }

    @Test
    void removeEpics() {
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        Epic epic2 = new Epic("Test addNewEpic2", "Test addNewEpic2 description");
        Epic epic3 = new Epic("Test addNewEpic3", "Test addNewEpic3 description");
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description", 1);
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description", 2);
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
        Assertions.assertNull(taskManager.getEpic(2), "Эпик не удален");
        Assertions.assertEquals(taskManager.getEpic(3), epics.get(1), "Эпики не совпадают.");
        Assertions.assertNull(taskManager.getSubtask(5), "Подзадача не удалена");

        taskManager.removeAllEpics();

        Assertions.assertEquals(taskManager.getAllEpics(), new ArrayList<Epic>(), "Эпики не удалены.");
        Assertions.assertEquals(taskManager.getAllSubtasks(), new ArrayList<Subtask>(), "Подзадачи не удалены.");
    }

    @Test
    void removeSubtasks() {
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        Epic epic2 = new Epic("Test addNewEpic2", "Test addNewEpic2 description");
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description", 1);
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description", 2);
        Subtask subtask3 = new Subtask("Test Subtask2", "Test Subtask2 description", 2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.removeSubtask(3);

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        Assertions.assertEquals(taskManager.getEpicSubtasks(1), new ArrayList<Subtask>(), "Подзадача не удалена");
        assertEquals(epic1.getChildTasksIds(), new ArrayList<Integer>(), "Подзадача не удалена");

        taskManager.removeAllSubtasks();

        Assertions.assertEquals(taskManager.getAllSubtasks(), new ArrayList<Subtask>(), "Подзадачи не удалены.");
        Assertions.assertEquals(taskManager.getEpicSubtasks(2), new ArrayList<Subtask>(), "Подзадачи не удалены");
    }

    @Test
    void shouldChangeEpicStatus() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Subtask subtask1 = new Subtask("Test Subtask1", "Test Subtask1 description", 1);
        Subtask subtask2 = new Subtask("Test Subtask2", "Test Subtask2 description", 1);
        Subtask updatedSubtask1 = new Subtask(2, "Test Subtask2",
                "Test Subtask2 description", 1, Status.DONE);
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
    void shouldShowOldTasksVersion() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description");
        Task task2 = new Task(1, "Test addNewTask2", "Test addNewTask2 description", Status.DONE);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);

        taskManager.createTask(task1);
        taskManager.getTask(1);
        taskManager.updateTask(task2);

        Assertions.assertEquals(tasks, taskManager.getHistory(), "История просмотра некорректна");
    }

    @Test
    void shouldShowTasksInHistoryAfterRewriting() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description");
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description");
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
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description");
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description");
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
        Task task = new Task("Test addNewTask1", "Test addNewTask1 description");
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", 2);
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
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description");
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description");
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description");
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