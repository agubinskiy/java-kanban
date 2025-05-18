package managers;

import enums.Status;
import exceptions.NotFoundException;
import exceptions.TasksCrossTimeException;
import history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager historyManager = Managers.getDefaultHistory();
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private Set<Task> priorityTasks = new TreeSet<>(Comparator.comparing((Task task) -> task.getStartTime().get()));
    private int counter = 0;


    @Override
    public void createTask(Task task) {
        if (isNotAnyTaskCrossTime(task)) {
            int taskId = ++counter;
            task.setTaskId(taskId);
            tasks.put(taskId, task);
            if (task.getStartTime().isPresent()) {
                priorityTasks.add(task);
            }
        } else {
            throw new TasksCrossTimeException("Ошибка пересечения времени выполнения при создании задачи");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int epicId = ++counter;
        epic.setTaskId(epicId);
        epics.put(epicId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (isNotAnyTaskCrossTime(subtask)) {
            int subtaskId = ++counter;
            subtask.setTaskId(subtaskId);
            int epicId = subtask.getEpicId();
            if (epics.containsKey(epicId)) {
                subtasks.put(subtaskId, subtask); //Добавили подзадачу в список подзадач
                Epic epic = epics.get(epicId);
                epic.getChildTasksIds().add(subtaskId); //Добавили подзадачу в список подзадач эпика
                updateEpicAfterCheck(epic);
            } else {
                counter--;
                throw new NotFoundException("Не найдено эпика с id: " + epicId);
            }
            if (subtask.getStartTime().isPresent()) {
                priorityTasks.add(subtask);
            }
        } else {
            throw new TasksCrossTimeException("Ошибка пересечения времени выполнения при создании подзадачи");
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
    public Task getTask(int taskId) throws NotFoundException {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
            return tasks.get(taskId);
        } else {
            throw new NotFoundException("Не найдено задачи с id: " + taskId);
        }
    }

    @Override
    public Epic getEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            historyManager.add(epics.get(epicId));
            return epics.get(epicId);
        } else {
            throw new NotFoundException("Не найдено эпика с id: " + epicId);
        }
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            historyManager.add(subtasks.get(subtaskId));
            return subtasks.get(subtaskId);
        } else {
            throw new NotFoundException("Не найдено подзадачи с id: " + subtaskId);
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        if (epics.containsKey(epicId)) {
            return getEpicSubtasks(epics.get(epicId));
        } else {
            throw new NotFoundException("Не найдено эпика с id: " + epicId);
        }
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getTaskId();
        if (isNotAnyTaskCrossTime(task)) {
            if (tasks.containsKey(taskId)) {
                if (tasks.get(taskId).getEndTime().isPresent()) {
                    priorityTasks.remove(tasks.get(taskId));
                }
                tasks.remove(taskId);
                tasks.put(taskId, task);
                if (task.getStartTime().isPresent()) {
                    priorityTasks.add(task);
                }
                System.out.println("Задача обновлена");
            } else {
                throw new NotFoundException("Не найдено задачи с id: " + taskId);
            }
        } else {
            throw new TasksCrossTimeException("Ошибка пересечения времени выполнения при обновлении задачи");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            epics.remove(epic.getTaskId());
            epics.put(epic.getTaskId(), epic);
            System.out.println("Эпик обновлён");
        } else {
            throw new NotFoundException("Не найдено эпика с id: " + epic.getTaskId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isNotAnyTaskCrossTime(subtask)) {
            if (subtasks.containsKey(subtask.getTaskId())) {
                int epicId = subtask.getEpicId();
                int subtaskId = subtask.getTaskId();
                if (subtasks.get(subtaskId).getEndTime().isPresent()) {
                    priorityTasks.remove(subtasks.get(subtaskId));
                }
                if (epics.containsKey(epicId)) {
                    Subtask oldSubtask = subtasks.get(subtaskId);
                    Epic newEpic = epics.get(epicId);
                    if (epicId != oldSubtask.getEpicId()) { //Если переносим в другой эпик, проверяем статус у старого
                        changeEpicOfSubtask(oldSubtask, newEpic);
                    }
                    subtasks.remove(subtaskId); //удалили задачу из списка подзадач
                    subtasks.put(subtaskId, subtask);
                    updateEpicAfterCheck(newEpic);
                    if (subtask.getStartTime().isPresent()) {
                        priorityTasks.add(subtask);
                    }
                    System.out.println("Подзадача обновлена");
                } else {
                    throw new NotFoundException("Не найдено эпика с id: " + epicId);
                }
            } else {
                throw new NotFoundException("Не найдено подзадачи с id: " + subtask.getTaskId());
            }
        } else {
            throw new TasksCrossTimeException("Ошибка пересечения времени выполнения при обновлении подзадачи");
        }
    }

    @Override
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            if (tasks.get(taskId).getEndTime().isPresent()) {
                priorityTasks.remove(tasks.get(taskId));
            }
            tasks.remove(taskId);
            historyManager.remove(taskId);
            System.out.println("Задача удалена");
        } else {
            throw new NotFoundException("Не найдено задачи с id: " + taskId);
        }
    }

    @Override
    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            for (int childTaskId : epic.getChildTasksIds()) { //Удаляем все связанные с эпиком подзадачи
                if (subtasks.get(childTaskId).getEndTime().isPresent()) {
                    priorityTasks.remove(subtasks.get(childTaskId));
                }
                subtasks.remove(childTaskId);
                historyManager.remove(childTaskId);
            }
            epics.remove(epicId); //Удаляем сам эпик
            historyManager.remove(epicId);
            System.out.println("Эпик удалён");
        } else {
            throw new NotFoundException("Не найдено эпика с id: " + epicId);
        }
    }

    @Override
    public void removeSubtask(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            if (subtasks.get(subtaskId).getEndTime().isPresent()) {
                priorityTasks.remove(subtasks.get(subtaskId));
            }
            Subtask subtask = subtasks.get(subtaskId);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.getChildTasksIds().remove((Integer) subtaskId); //Удалили подзадачу из списка подзадач эпика
            subtasks.remove(subtaskId);
            updateEpicAfterCheck(epic); //Обновили статус эпика
            System.out.println("Подзадача удалена");
            historyManager.remove(subtaskId);
        } else {
            throw new NotFoundException("Не найдено подзадачи с id: " + subtaskId);
        }
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            if (tasks.get(id).getEndTime().isPresent()) {
                priorityTasks.remove(tasks.get(id));
            }
        }
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            epic.getChildTasksIds().clear();
        }
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            if (subtasks.get(id).getEndTime().isPresent()) {
                priorityTasks.remove(subtasks.get(id));
            }
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики удалены");
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            if (subtasks.get(id).getEndTime().isPresent()) {
                priorityTasks.remove(subtasks.get(id));
            }
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getChildTasksIds().clear();
            updateEpicAfterCheck(epic);
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

    public Duration epicCheckDuration(Epic epic) {
        if (!getEpicSubtasks(epic).isEmpty()) {
            List<Subtask> epicSubtasks = getEpicSubtasks(epic);
            return epicSubtasks.stream()
                    .map(task -> task.getDuration().orElse(Duration.ZERO))
                    .reduce(Duration.ZERO, Duration::plus);
        }
        return null; //если подзадач нет, продолжительность 0
    }

    public LocalDateTime epicCheckStartTime(Epic epic) {
        if (!getEpicSubtasks(epic).isEmpty()) {
            List<Subtask> epicSubtasks = getEpicSubtasks(epic);
            return epicSubtasks.stream()
                    .filter(task -> task.getStartTime().isPresent())
                    .map(task -> task.getStartTime().get())
                    .min(Comparator.naturalOrder())
                    .orElse(null);
        }
        return null; //если подзадач нет, продолжительность 0
    }

    public LocalDateTime epicCheckEndTime(Epic epic) {
        if (!getEpicSubtasks(epic).isEmpty()) {
            List<Subtask> epicSubtasks = getEpicSubtasks(epic);
            return epicSubtasks.stream()
                    .filter(task -> task.getEndTime().isPresent())
                    .map(task -> task.getEndTime().get())
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }
        return null; //если подзадач нет, продолжительность 0
    }

    public void updateEpicAfterCheck(Epic epic) {
        epic.setStatus(epicCheckStatus(epic)); //Обновили статус эпика
        epic.setDuration(epicCheckDuration(epic));
        epic.setStartTime(epicCheckStartTime(epic));
        epic.setEndTime(epicCheckEndTime(epic));
    }

    public Set<Task> getPrioritizedTasks() {
        return priorityTasks;
    }

    public boolean isTasksCrossTime(Task task1, Task task2) {
        if (task1.getEndTime().isPresent() && task2.getEndTime().isPresent()) { //если endTime не null, то и startTime
            return (task1.getEndTime().get().isAfter(task2.getStartTime().get())) &&
                    (task2.getEndTime().get().isAfter(task1.getStartTime().get()));
        } else return false;
    }

    public boolean isNotAnyTaskCrossTime(Task task) {
        if (task.getStartTime().isEmpty() || task.getDuration().isEmpty() || getPrioritizedTasks().isEmpty()) {
            return true;
        } else {
            return getPrioritizedTasks().stream()
                    .filter(taskFromList -> taskFromList.getTaskId() != task.getTaskId())
                    .noneMatch(taskFromList -> isTasksCrossTime(task, taskFromList));
        }
    }

    private void changeEpicOfSubtask(Subtask subtask, Epic newEpic) {
        Epic oldEpic = epics.get(subtask.getEpicId());
        oldEpic.getChildTasksIds().remove((Integer) subtask.getTaskId()); //Удалили подзадачу из списка старого эпика
        updateEpicAfterCheck(oldEpic);
        newEpic.getChildTasksIds().add(subtask.getTaskId()); //Добавили подзадачу в список нового эпика
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return subtasks.values().stream()
                .filter(subtask -> epic.getTaskId() == subtask.getEpicId())
                .collect(Collectors.toList());
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
