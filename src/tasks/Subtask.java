package tasks;

import enums.Status;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, long minutes, LocalDateTime startTime, int epicId) {
        super(name, description, minutes, startTime);
        this.epicId = epicId;
    }

    public Subtask(int taskId, String name, String description, long minutes, LocalDateTime startTime,
                   int epicId, Status status) {
        super(taskId, name, description, minutes, startTime, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.toString() +
                "," + epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
