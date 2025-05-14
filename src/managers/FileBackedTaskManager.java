package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import enums.Status;
import tasks.Subtask;
import tasks.Task;
import enums.TaskParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public File fileOfTasks;

    //конструктор класса
    public FileBackedTaskManager(File file) {
        fileOfTasks = file;
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(String.valueOf(fileOfTasks))) {
            fileWriter.write("id,type,name,status,description,duration,startTime,epic" + "\n");
            List<Task> listOfTasks = getAllTasks();
            for (Task task : listOfTasks) {
                fileWriter.write(task.toString() + "\n");
            }
            List<Epic> listOfEpics = getAllEpics();
            for (Epic epic : listOfEpics) {
                fileWriter.write(epic.toString() + "\n");
            }
            List<Subtask> listOfSubtasks = getAllSubtasks();
            for (Subtask subtask : listOfSubtasks) {
                fileWriter.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            String allTasksInFile = Files.readString(file.toPath());
            String[] split = allTasksInFile.split("\n");
            int maxId = 0;
            for (int i = 1; i < split.length; i++) {
                Task newTask = fileBackedTaskManager.fromString(split[i]);
                assert newTask != null;
                Class<? extends Task> type = newTask.getClass();
                if (type.equals(Subtask.class)) {
                    fileBackedTaskManager.addSubtask((Subtask) newTask);
                } else if (type.equals(Epic.class)) {
                    fileBackedTaskManager.addEpic((Epic) newTask);
                } else {
                    fileBackedTaskManager.addTask(newTask);
                }
                if (maxId < newTask.getTaskId()) {
                    maxId = newTask.getTaskId();
                }
                //Обновляем счетчик для корректной генерации id при создании новых задач
                fileBackedTaskManager.setCounter(maxId);
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла");
        }
    }

    private Task fromString(String string) {
        String[] split = string.split(",");
        return switch (split[1]) {
            case "TASK" -> new Task(Integer.parseInt(split[TaskParameters.ID.getIndex()]),
                    split[TaskParameters.NAME.getIndex()],
                    split[TaskParameters.DESCRIPTION.getIndex()],
                    Long.parseLong((split[TaskParameters.DURATION.getIndex()])),
                    LocalDateTime.parse(split[TaskParameters.STARTTIME.getIndex()]),
                    Status.valueOf(split[TaskParameters.STATUS.getIndex()]));
            case "EPIC" -> new Epic(Integer.parseInt(split[TaskParameters.ID.getIndex()]),
                    split[TaskParameters.NAME.getIndex()],
                    split[TaskParameters.DESCRIPTION.getIndex()]);
            case "SUBTASK" -> new Subtask(Integer.parseInt(split[TaskParameters.ID.getIndex()]),
                    split[TaskParameters.NAME.getIndex()],
                    split[TaskParameters.DESCRIPTION.getIndex()],
                    Long.parseLong((split[TaskParameters.DURATION.getIndex()])),
                    LocalDateTime.parse(split[TaskParameters.STARTTIME.getIndex()]),
                    Integer.parseInt(split[TaskParameters.EPIC.getIndex()]),
                    Status.valueOf(split[TaskParameters.STATUS.getIndex()]));
            default -> null;
        };
    }

    private void addTask(Task task) {
        getTasks().put(task.getTaskId(), task);
    }

    private void addEpic(Epic epic) {
        getEpics().put(epic.getTaskId(), epic);
    }

    private void addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        getSubtasks().put(subtask.getTaskId(), subtask);
        if (getEpics().containsKey(epicId)) {
            getSubtasks().put(subtask.getTaskId(), subtask); //Добавили подзадачу в список подзадач
            Epic epic = getEpics().get(epicId);
            epic.getChildTasksIds().add(subtask.getTaskId()); //Добавили подзадачу в список подзадач эпика
        }
    }


    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }
}
