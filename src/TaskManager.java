import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    Scanner scanner;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static int counter = 0;

    TaskManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public void createTask() {
        counter++;
        int taskId = counter;
        System.out.println("Введите название задачи");
        String taskName = scanner.nextLine();
        System.out.println("Введите описание задачи");
        String taskDescription = scanner.nextLine();
        Task task = new Task(taskId, taskName, taskDescription);
        tasks.put(taskId, task);
    }

    public void createEpic() {
        counter++;
        int epicId = counter;
        System.out.println("Введите название эпика");
        String taskName = scanner.nextLine();
        System.out.println("Введите описание эпика");
        String taskDescription = scanner.nextLine();
        Epic epic = new Epic(epicId, taskName, taskDescription);
        epics.put(epicId, epic);
    }

    public void createSubtask() {
        counter++;
        int subtaskId = counter;
        System.out.println("Введите название подзадачи");
        String taskName = scanner.nextLine();
        System.out.println("Введите описание подзадачи");
        String taskDescription = scanner.nextLine();
        System.out.println("Введите Id эпика");
        int epicId = scanner.nextInt();
        if(epics.containsKey(epicId)) {
            scanner.nextLine();
            Subtask subtask = new Subtask(subtaskId, taskName, taskDescription, epicId);
            subtasks.put(subtaskId, subtask); //Добавили подзадачу в список подзадач
            Epic epic = epics.get(epicId);
            epic.childTasks.add(subtask); //Добавили подзадачу в список подзадач эпика
            epic.setStatus(epic.epicCheckStatus()); //Обновили статус эпика
        } else {
            System.out.println("Ошибка, эпика с таким идентификатором не существует");
            counter--;
        }
    }

    public void printAllTasks() {
            System.out.println(tasks);
    }

    public void printAllEpics() {
        System.out.println(epics);
    }

    public void printAllSubtasks() {
        System.out.println(subtasks);
    }

    public void printTask() {
        System.out.println("Введите идентификатор задачи");
        int taskId = scanner.nextInt();
        scanner.nextLine();
        if(tasks.containsKey(taskId)) {
            System.out.println(tasks.get(taskId));
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
        }
    }

    public void printEpic() {
        System.out.println("Введите идентификатор эпика");
        int epicId = scanner.nextInt();
        scanner.nextLine();
        if(epics.containsKey(epicId)) {
            System.out.println(epics.get(epicId));
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
        }
    }

    public void printSubtask() {
        System.out.println("Введите идентификатор подзадачи");
        int subtaskId = scanner.nextInt();
        scanner.nextLine();
        if(subtasks.containsKey(subtaskId)) {
            System.out.println(subtasks.get(subtaskId));
        } else {
            System.out.println("Не найдено подзадачи с таким идентификатором");
        }
    }

    public void printEpicSubtasks() {
        System.out.println("Введите идентификатор эпика");
        int epicId = scanner.nextInt();
        scanner.nextLine();
        if(epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            for(Subtask subtask: epic.childTasks) {
                System.out.println(subtask);
            }
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
        }
    }

    public void updateTask() {
        System.out.println("Введите идентификатор задачи");
        int taskId = scanner.nextInt();
        scanner.nextLine();
        if(tasks.containsKey(taskId)) {
            System.out.println("Введите название задачи");
            String taskName = scanner.nextLine();
            System.out.println("Введите описание задачи");
            String taskDescription = scanner.nextLine();
            System.out.println("Введите статус задачи");
            Status status = Status.valueOf(scanner.nextLine());
            Task task = new Task(taskId, taskName, taskDescription, status);
            tasks.remove(task.getTaskId());
            tasks.put(task.getTaskId(), task);
            System.out.println("Задача обновлена");
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
        }
    }

    public void updateEpic() {
        System.out.println("Введите идентификатор эпика");
        int epicId = scanner.nextInt();
        scanner.nextLine();
        if(epics.containsKey(epicId)) {
            System.out.println("Введите название эпика");
            String epicName = scanner.nextLine();
            System.out.println("Введите описание эпика");
            String epicDescription = scanner.nextLine();
            Epic epic = new Epic(epicId, epicName, epicDescription);
            epics.remove(epic.getTaskId());
            epics.put(epic.getTaskId(), epic);
            System.out.println("Эпик обновлён");
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
        }
    }

    public void updateSubtask() {
        System.out.println("Введите идентификатор подзадачи");
        int subtaskId = scanner.nextInt();
        scanner.nextLine();
        if (subtasks.containsKey(subtaskId)) {
            System.out.println("Введите название подзадачи");
            String subtaskName = scanner.nextLine();
            System.out.println("Введите описание подзадачи");
            String subtaskDescription = scanner.nextLine();
            System.out.println("Введите идентификатор эпика");
            int epicId = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Введите статус задачи");
            Status status = Status.valueOf(scanner.nextLine());
            if (epics.containsKey(epicId)) {
                Subtask oldSubtask = subtasks.get(subtaskId);
                Subtask newSubtask = new Subtask(subtaskId, subtaskName, subtaskDescription, epicId, status);
                Epic newEpic = epics.get(epicId);
                if(epicId != oldSubtask.getEpicId()) { //Если переносим в другой эпик, проверяем статус у старого
                    Epic oldEpic = epics.get(oldSubtask.getEpicId());
                    oldEpic.childTasks.remove(oldSubtask); //Удалили подзадачу из списка старого эпика
                    oldEpic.setStatus(oldEpic.epicCheckStatus()); //Обновили статус у старого
                }
                newEpic.childTasks.remove(oldSubtask); //удалили задачу из списка подзадач эпика
                newEpic.childTasks.add(newSubtask); //Добавили подзадачу в список нового эпика
                subtasks.remove(oldSubtask.getTaskId()); //удалили задачу из списка подзадач
                subtasks.put(subtaskId, newSubtask);
                newEpic.setStatus(newEpic.epicCheckStatus());
                System.out.println("Подзадача обновлена");
            } else {
                System.out.println("Не найдено эпика с таким идентификатором");
            }
        } else {
            System.out.println("Не найдено подзадачи с таким идентификатором");
        }
    }

    public void removeTask() {
        System.out.println("Введите идентификатор задачи");
        int taskId = scanner.nextInt();
        scanner.nextLine();
        if(tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            System.out.println("Задача удалена");
        } else {
            System.out.println("Не найдено задачи с таким идентификатором");
        }
    }

    public void removeEpic() {
        System.out.println("Введите идентификатор эпика");
        int epicId = scanner.nextInt();
        scanner.nextLine();
        if(epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            for(Subtask childTask: epic.childTasks) { //Удаляем все связанные с эпиком подзадачи
                int id = childTask.getTaskId();
                subtasks.remove(id);
            }
            epic.childTasks.clear(); //Очищаем список подзадач у эпика
            epics.remove(epicId); //Удаляем сам эпик
            System.out.println("Эпик удалён");
        } else {
            System.out.println("Не найдено эпика с таким идентификатором");
        }
    }

    public void removeSubtask() {
        System.out.println("Введите идентификатор подзадачи");
        int subtaskId = scanner.nextInt();
        scanner.nextLine();
        if(subtasks.containsKey(subtaskId)) {
            Subtask subtask = subtasks.get(subtaskId);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            epic.childTasks.remove(subtask); //Удалили подзадачу из списка подзадач эпика
            epic.setStatus(epic.epicCheckStatus()); //Обновили статус эпика
            subtasks.remove(subtaskId);
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
            epic.childTasks.clear();
        }
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики удалены");
    }

    public void removeAllSubtasks() {
        for(Epic epic: epics.values()) {
            epic.childTasks.clear();
            epic.setStatus(epic.epicCheckStatus());
        }
        subtasks.clear();
        System.out.println("Все подзадачи удалены");
    }

}
