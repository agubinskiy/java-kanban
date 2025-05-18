package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Epic extends Task {
    private List<Integer> childTasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic() {
        super();
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int taskId, String name, String description) {
        super(taskId, name, description);
    }

    public Epic(int taskId, String name, String description, long minutes) {
        super(taskId, name, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(childTasksIds, epic.childTasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), childTasksIds);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    public List<Integer> getChildTasksIds() {
        return childTasksIds;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
