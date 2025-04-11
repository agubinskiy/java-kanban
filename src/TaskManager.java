import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Task getTask(int taskId);

    Epic getEpic(int epicId);

    Subtask getSubtask(int subtaskId);

    List<Subtask> getEpicSubtasks(int epicId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTask(int taskId);

    void removeEpic(int epicId);

    void removeSubtask(int subtaskId);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    List<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getHistory();
}
