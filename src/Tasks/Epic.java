package Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> childTasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int taskId, String name, String description) {
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
        return "Tasks.Epic{" +
                "childTasksIds=" + childTasksIds +
                "} " + super.toString();
    }

    public List<Integer> getChildTasksIds() {
        return childTasksIds;
    }
}
