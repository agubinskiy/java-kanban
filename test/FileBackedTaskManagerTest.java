import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private File tempFile;
    private String columns = "id,type,name,status,description,epic" + "\n";

    @BeforeEach
    public void beforeEach() {
        try {
            tempFile = File.createTempFile("temp", null);
            try (Writer fileWriter = new FileWriter(String.valueOf(tempFile))) {
                fileWriter.write(columns);
            }
            taskManager = new FileBackedTaskManager(tempFile);
            taskManager.getTasks().clear();
            taskManager.getEpics().clear();
            taskManager.getSubtasks().clear();
            Task.setCounter(0);
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла: " + e.getMessage());
        }
    }


    @Test
    void shouldLoadTaskFromFile() throws IOException {
        try (Writer fileWriter = new FileWriter(String.valueOf(tempFile), true)) {
            fileWriter.write("1,TASK,Задача1,NEW,Описание задачи1" + "\n");
        }
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не найдены.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(tasks.get(0), taskManager.getTask(1), "Задача некорректна.");
    }

    @Test
    void shouldLoadEpicAndSubtaskFromFile() throws IOException {
        try (Writer fileWriter = new FileWriter(String.valueOf(tempFile), true)) {
            fileWriter.write("1,EPIC,Эпик1,NEW,Описание эпика1" + "\n");
            fileWriter.write("2,SUBTASK,Подзадача1,NEW,Описание подзадачи1,1" + "\n");
        }
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(epics, "Эпики не найдены.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epics.get(0), taskManager.getEpic(1), "Эпик некорректен.");

        assertNotNull(subtasks, "Подзадачи не найдены.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtasks.get(0), taskManager.getSubtask(2), "Подзадача некорректна.");

        assertEquals(subtasks, taskManager.getEpicSubtasks(1), "Неверное количество подзадач.");
    }

    @Test
    void shouldAddNewTaskInFile() throws IOException {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        String allTasksInFile = Files.readString(tempFile.toPath());

        assertNotNull(allTasksInFile, "Задачи не возвращаются.");
        assertEquals(allTasksInFile, columns + "1,TASK,Test addNewTask,NEW,Test addNewTask description" + "\n", "Задача некорректно добавлена в файл");
    }

    @Test
    void shouldAddNewEpicAndSubtaskInFile() throws IOException {
        Epic epic = new Epic("Test Epic", "Test Epic description");
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        String allTasksInFile = Files.readString(tempFile.toPath());


        assertNotNull(allTasksInFile, "Задачи не возвращаются.");
        assertEquals(allTasksInFile, columns + "1,EPIC,Test Epic,NEW,Test Epic description" + "\n" + "2,SUBTASK,Test Subtask,NEW,Test Subtask description,1" + "\n", "Эпик с подзадачей некорректно добавлены в файл");
    }
}
