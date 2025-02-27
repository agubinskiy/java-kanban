import java.util.Objects;

public class Task {
    private int taskId;
    private String name;
    private String description;
    private Status status;
    private static int counter = 0;

    //Конструктор для создания новой задачи
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.taskId = ++counter;
        status = Status.NEW;
    }

    //Конструктор для обновления эпика
    public Task(int taskId, String name, String description) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        status = Status.NEW;
    }

    //Конструктор для обновления задачи
    public Task(int taskId, String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(name, task.name) && Objects.equals(description,
                task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, taskId, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "Id=" + taskId +
                ", Название:'" + name + '\'' +
                ", Описание:'" + description + '\'' +
                ", Статус:" + status +
                '}' + '\n';
    }

    public int getTaskId() {
        return taskId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
