package History;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> tasksHistory = new ArrayList<>(10);

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void add(Task task) {
        if(tasksHistory.size() == 10) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(task);
    }
}
