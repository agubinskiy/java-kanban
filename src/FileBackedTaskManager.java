import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public File fileOfTasks;

    //конструктор класса
    public FileBackedTaskManager(File file) {
        fileOfTasks = file;
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(String.valueOf(fileOfTasks))) {
            fileWriter.write("id,type,name,status,description,epic" + "\n");
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
                if (newTask instanceof Subtask) {
                    int epicId = ((Subtask) newTask).getEpicId();
                    subtasks.put(newTask.getTaskId(), (Subtask) newTask);
                    if (epics.containsKey(epicId)) {
                        subtasks.put(newTask.getTaskId(), (Subtask) newTask); //Добавили подзадачу в список подзадач
                        Epic epic = epics.get(epicId);
                        epic.getChildTasksIds().add(newTask.getTaskId()); //Добавили подзадачу в список подзадач эпика
                    }
                } else if (newTask instanceof Epic) {
                    epics.put(newTask.getTaskId(), (Epic) newTask);
                } else if (newTask instanceof Task) {
                    tasks.put(newTask.getTaskId(), newTask);
                }
                assert newTask != null;
                if (maxId < newTask.getTaskId()) {
                    maxId = newTask.getTaskId();
                }
                //Обновляем счетчик для корректной генерации id при создании новых задач
                fileBackedTaskManager.setCounter(maxId);
                Task.setCounter(maxId);
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла");
        }
    }

    private Task fromString(String string) {
        String[] split = string.split(",");
        return switch (split[1]) {
            case "TASK" -> new Task(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]));
            case "EPIC" -> new Epic(Integer.parseInt(split[0]), split[2], split[4]);
            case "SUBTASK" -> new Subtask(Integer.parseInt(split[0]), split[2], split[4], Integer.parseInt(split[5]),
                    Status.valueOf(split[3]));
            default -> null;
        };
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
