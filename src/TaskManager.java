import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int counter = 0;


    public void createTask(Task task) {
        int taskId = ++counter;
        tasks.put(taskId, task);
    }

    public void createEpic(Epic epic) {
        int epicId = ++counter;
        epics.put(epicId, epic);
    }

    public void createSubtask(Subtask subtask) {
        int subtaskId = ++counter;
        int epicId = subtask.getEpicId();
        if(epics.containsKey(epicId)) {
            subtasks.put(subtaskId, subtask); //Добавили подзадачу в список подзадач
            Epic epic = epics.get(epicId);
            epic.getChildTasksIds().add(subtaskId); //Добавили подзадачу в список подзадач эпика
            epic.setStatus(epicCheckStatus(epic)); //Обновили статус эпика
        } else {
            System.out.println("Ошибка, эпика с таким идентификатором не существует");
            counter--;
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTask(int taskId) {
        if(tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
            return null;
        }
    }

    public Epic printEpic(int epicId) {
        if(epics.containsKey(epicId)) {
            return epics.get(epicId);
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
            return null;
        }
    }

    public Subtask printSubtask(int subtaskId) {
        if(subtasks.containsKey(subtaskId)) {
            return subtasks.get(subtaskId);
        } else {
            System.out.println("Не найдено подзадачи с таким идентификатором");
            return null;
        }
    }

    public List<Subtask> printEpicSubtasks(int epicId) {
        if(epics.containsKey(epicId)) {
            return getEpicSubtasks(epics.get(epicId));
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
            return null;
        }
    }

    public void updateTask(Task task) {
        if(tasks.containsKey(task.getTaskId())) {
            tasks.remove(task.getTaskId());
            tasks.put(task.getTaskId(), task);
            System.out.println("Задача обновлена");
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
        }
    }

    public void updateEpic(Epic epic) {
        if(epics.containsKey(epic.getTaskId())) {
            epics.remove(epic.getTaskId());
            epics.put(epic.getTaskId(), epic);
            System.out.println("Эпик обновлён");
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getTaskId())) {
            int epicId = subtask.getEpicId();
            int subtaskId = subtask.getTaskId();
            if (epics.containsKey(epicId)) {
                Subtask oldSubtask = subtasks.get(subtaskId);
                Epic newEpic = epics.get(epicId);
                if(epicId != oldSubtask.getEpicId()) { //Если переносим в другой эпик, проверяем статус у старого
                    Epic oldEpic = epics.get(oldSubtask.getEpicId());
                    oldEpic.getChildTasksIds().remove((Integer)subtaskId); //Удалили подзадачу из списка старого эпика
                    oldEpic.setStatus(epicCheckStatus(oldEpic)); //Обновили статус у старого
                    newEpic.getChildTasksIds().add(subtaskId); //Добавили подзадачу в список нового эпика
                }
                subtasks.remove(subtaskId); //удалили задачу из списка подзадач
                subtasks.put(subtaskId, subtask);
                newEpic.setStatus(epicCheckStatus(newEpic));
                System.out.println("Подзадача обновлена");
            } else {
                System.out.println("Не найдено эпика с таким идентификатором");
            }
        } else {
            System.out.println("Не найдено подзадачи с таким идентификатором");
        }
    }

    public void removeTask(int taskId) {
        if(tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            System.out.println("Задача удалена");
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
        }
    }

    public void removeEpic(int epicId) {
        if(epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            for(int childTaskId: epic.getChildTasksIds()) { //Удаляем все связанные с эпиком подзадачи
                subtasks.remove(childTaskId);
            }
            epics.remove(epicId); //Удаляем сам эпик
            System.out.println("Эпик удалён");
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
        }
    }

    public void removeSubtask(int subtaskId) {
        if(subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.getChildTasksIds().remove((Integer) subtaskId); //Удалили подзадачу из списка подзадач эпика
            subtasks.remove(subtaskId);
            epic.setStatus(epicCheckStatus(epic)); //Обновили статус эпика
            System.out.println("Подзадача удалена");
        } else {
            System.out.println("Не найдено подзадачи с таким идентификатором");
        }
    }

    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    public void removeAllEpics() {
        for(Epic epic: epics.values()) {
            epic.getChildTasksIds().clear();
        }
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики удалены");
    }

    public void removeAllSubtasks() {
        for(Epic epic: epics.values()) {
            epic.getChildTasksIds().clear();
            epic.setStatus(epicCheckStatus(epic));
        }
        subtasks.clear();
        System.out.println("Все подзадачи удалены");
    }

    public Status epicCheckStatus(Epic epic) {
        if(!epic.getChildTasksIds().isEmpty()) {
            List<Subtask> epicSubtasks = getEpicSubtasks(epic);
            Status status = epicSubtasks.getFirst().getStatus();
            if(epicSubtasks.size() > 1) {
                for (int i = 1; i < epicSubtasks.size(); i++) {
                    if (epicSubtasks.get(i).getStatus() != status) { //если хотя бы у двух подзадач разный статус
                        return Status.IN_PROGRESS;
                    }
                } return status; //Если у всех подзадач одинаковый статус, у эпика будет такой же
            }
            return status; //если в эпике всего одна подзадача, возвращаем ее статус
        }
        return Status.NEW; //если подзадач нет, статус эпика NEW
    }

    public List<Subtask> getEpicSubtasks(Epic epic) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<Subtask>();
        for (Subtask subtask : subtasks.values()) {
            if (epic.getTaskId() == subtask.getEpicId()) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

}
