import history.HistoryManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager historyManager = Managers.getDefaultHistory();
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int counter = 0;


    @Override
    public void createTask(Task task) {
        int taskId = ++counter;
        tasks.put(taskId, task);
    }

    @Override
    public void createEpic(Epic epic) {
        int epicId = ++counter;
        epics.put(epicId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int subtaskId = ++counter;
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            subtasks.put(subtaskId, subtask); //Добавили подзадачу в список подзадач
            Epic epic = epics.get(epicId);
            epic.getChildTasksIds().add(subtaskId); //Добавили подзадачу в список подзадач эпика
            epic.setStatus(epicCheckStatus(epic)); //Обновили статус эпика
        } else {
            System.out.println("Ошибка, эпика с таким идентификатором не существует");
            counter--;
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
            return tasks.get(taskId);
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
            return null;
        }
    }

    @Override
    public Epic getEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            historyManager.add(epics.get(epicId));
            return epics.get(epicId);
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
            return null;
        }
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            historyManager.add(subtasks.get(subtaskId));
            return subtasks.get(subtaskId);
        } else {
            System.out.println("Не найдено подзадачи с таким идентификатором");
            return null;
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        if (epics.containsKey(epicId)) {
            return getEpicSubtasks(epics.get(epicId));
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
            return null;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            tasks.remove(task.getTaskId());
            tasks.put(task.getTaskId(), task);
            System.out.println("Задача обновлена");
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            epics.remove(epic.getTaskId());
            epics.put(epic.getTaskId(), epic);
            System.out.println("Эпик обновлён");
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getTaskId())) {
            int epicId = subtask.getEpicId();
            int subtaskId = subtask.getTaskId();
            if (epics.containsKey(epicId)) {
                Subtask oldSubtask = subtasks.get(subtaskId);
                Epic newEpic = epics.get(epicId);
                if (epicId != oldSubtask.getEpicId()) { //Если переносим в другой эпик, проверяем статус у старого
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

    @Override
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            historyManager.remove(taskId);
            System.out.println("Задача удалена");
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
        }
    }

    @Override
    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            for (int childTaskId: epic.getChildTasksIds()) { //Удаляем все связанные с эпиком подзадачи
                subtasks.remove(childTaskId);
                historyManager.remove(childTaskId);
            }
            epics.remove(epicId); //Удаляем сам эпик
            historyManager.remove(epicId);
            System.out.println("Эпик удалён");
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
        }
    }

    @Override
    public void removeSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.getChildTasksIds().remove((Integer) subtaskId); //Удалили подзадачу из списка подзадач эпика
            subtasks.remove(subtaskId);
            epic.setStatus(epicCheckStatus(epic)); //Обновили статус эпика
            System.out.println("Подзадача удалена");
            historyManager.remove(subtaskId);
        } else {
            System.out.println("Не найдено подзадачи с таким идентификатором");
        }
    }

    @Override
    public void removeAllTasks() {
        for (Integer id: tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic: epics.values()) {
            epic.getChildTasksIds().clear();
        }
        for (Integer id: subtasks.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id: epics.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики удалены");
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id: subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic: epics.values()) {
            epic.getChildTasksIds().clear();
            epic.setStatus(epicCheckStatus(epic));
        }
        System.out.println("Все подзадачи удалены");
    }

    public Status epicCheckStatus(Epic epic) {
        if (!getEpicSubtasks(epic).isEmpty()) {
            List<Subtask> epicSubtasks = getEpicSubtasks(epic);
            Status status = epicSubtasks.get(0).getStatus();
            if (epicSubtasks.size() > 1) {
                for (int i = 1; i < epicSubtasks.size(); i++) {
                    if (epicSubtasks.get(i).getStatus() != status) { //если хотя бы у двух подзадач разный статус
                        return Status.IN_PROGRESS;
                    }
                }
                return status; //Если у всех подзадач одинаковый статус, у эпика будет такой же
            }
            return status; //если в эпике всего одна подзадача, возвращаем ее статус
        }
        return Status.NEW; //если подзадач нет, статус эпика NEW
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        List<Subtask> epicSubtasks = new ArrayList<Subtask>();
        for (Subtask subtask : subtasks.values()) {
            if (epic.getTaskId() == subtask.getEpicId()) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}
