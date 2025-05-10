import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new InMemoryTaskManager();

        //Нужно для детального тестирования файлового менеджера, очень не хочется удалять
        //TaskManager taskManager = Managers.getDefault();
        //taskManager = FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.home"),
        //        "testFile.txt"));

        Task task1 = new Task("Задача1", "Описание задачи1",
                LocalDateTime.of(2025, 5, 1, 14, 0));
        Task task2 = new Task("Задача2", "Описание задачи2",
                30, LocalDateTime.of(2025, 5, 1, 13, 0));
        Task task3 = new Task(1, "Задача1", "Описание задачи3",
                30, LocalDateTime.of(2025, 5, 1, 14, 0), Status.DONE);

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                30, LocalDateTime.of(2025, 5, 1, 15, 0), 3);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                30, LocalDateTime.of(2025, 5, 1, 16, 0), 4);
        Subtask subtask3 = new Subtask(5, "Подзадача3", "Описание подзадачи3",
                30, LocalDateTime.of(2025, 5, 1, 17, 0), 4,
                Status.DONE);

        taskManager.createTask(task1);
        System.out.println(taskManager.getTask(1));
        taskManager.createTask(task2);
        taskManager.updateTask(task3);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getSubtask(5));


        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
