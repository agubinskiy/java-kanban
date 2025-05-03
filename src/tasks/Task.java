package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private final int taskId;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private static int counter = 0;

    //Конструктор для создания новой задачи
    public Task(String name, String description, long minutes, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.taskId = ++counter;
        this.duration = Duration.ofMinutes(minutes);
        this.startTime = startTime;
        status = Status.NEW;
    }

    //Конструктор для создания новой задачи без времени начала
    public Task(String name, String description, long minutes) {
        this.name = name;
        this.description = description;
        this.taskId = ++counter;
        this.duration = Duration.ofMinutes(minutes);
        status = Status.NEW;
    }

    //Конструктор для создания новой задачи без продолжительности
    public Task(String name, String description, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.taskId = ++counter;
        this.startTime = startTime;
        status = Status.NEW;
    }

    //Конструктор для создания эпика
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
    public Task(int taskId, String name, String description, long minutes, LocalDateTime startTime,
                Status status) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        this.duration = Duration.ofMinutes(minutes);
        this.startTime = startTime;
        this.status = status;
    }

    public static void setCounter(int counter) {
        Task.counter = counter;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (startTime != null && duration != null) {
            return Optional.of(startTime.plus(duration));
        } else {
            return Optional.empty();
        }
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
        if (duration != null) {
            return taskId +
                    "," + getClass().toString().substring(12).toUpperCase() +
                    "," + name +
                    "," + status +
                    "," + description +
                    "," + duration.toMinutes() +
                    "," + startTime;
        } else {
            return taskId +
                    "," + getClass().toString().substring(12).toUpperCase() +
                    "," + name +
                    "," + status +
                    "," + description +
                    "," + null +
                    "," + startTime;
        }
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

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
