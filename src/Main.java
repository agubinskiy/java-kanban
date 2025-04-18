import tasks.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("Задача1", "Описание задачи1");
        Task task2 = new Task("Задача2", "Описание задачи2");
        Task task3 = new Task(1, "Задача1", "Описание задачи3", Status.DONE);

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", 3);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", 4);
        Subtask subtask3 = new Subtask(5, "Подзадача3", "Описание подзадачи3", 4,
                Status.DONE);

        taskManager.createTask(task1);
        //System.out.println(taskManager.getTask(1));
        taskManager.createTask(task2);
        taskManager.updateTask(task3);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        //taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getSubtask(5));

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        //System.out.println(taskManager.getEpicSubtasks(4));
        System.out.println(taskManager.getSubtask(5));
        taskManager.createTask(task1);

        //taskManager.removeAllEpics();
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
