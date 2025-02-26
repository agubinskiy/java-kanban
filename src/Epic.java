import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<Subtask> childTasks = new ArrayList<>();

    public Epic(int taskId, String name, String description) {
        super(taskId, name, description);
    }

    public Status epicCheckStatus() {
        if(!childTasks.isEmpty()) {
            Status status = childTasks.getFirst().getStatus();
            if(childTasks.size() > 1) {
                for (int i = 1; i < childTasks.size(); i++) {
                    if (childTasks.get(i).getStatus() != status) { //если хотя бы у двух подзадач разный статус
                        return Status.IN_PROGRESS;
                    }
                } return status; //Если у всех подзадач одинаковый статус, у эпика будет такой же
            }
            return status; //если в эпике всего одна подзадача, возвращаем ее статус
        }
        return Status.NEW; //если подзадач нет, статус эпика NEW
    }
}
