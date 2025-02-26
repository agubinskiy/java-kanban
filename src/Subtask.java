public class Subtask extends Task {
    private int epicId;

    public Subtask(int taskId, String name, String description, int epicId) {
        super(taskId, name, description);
        this.epicId = epicId;
    }

    public Subtask(int taskId, String name, String description, int epicId, Status status) {
        super(taskId, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                "} " + super.toString();
    }
}
