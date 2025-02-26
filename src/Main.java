import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager(scanner);

        while(true) {
            printTypesMenu();
            int type = scanner.nextInt();
            scanner.nextLine();
            if (type == 1) {
                while(true) {
                    printMainMenu();
                    int cmd = scanner.nextInt();
                    scanner.nextLine();
                    if (cmd == 1) {
                        taskManager.printAllTasks();
                    } else if (cmd == 2) {
                        taskManager.printTask();
                    } else if (cmd == 3) {
                        taskManager.createTask();
                    } else if (cmd == 4) {
                        taskManager.updateTask();
                    } else if (cmd == 5) {
                        taskManager.removeTask();
                    } else if (cmd == 6) {
                        taskManager.removeAllTasks();
                    } else if (cmd == 7) {
                        break;
                    }
                }
            } else if (type == 2) {
                while(true) {
                printEpicMainMenu();
                int cmd = scanner.nextInt();
                scanner.nextLine();
                if (cmd == 1) {
                    taskManager.printAllEpics();
                } else if (cmd == 2) {
                    taskManager.printEpic();
                } else if (cmd == 3) {
                    taskManager.printEpicSubtasks();
                } else if (cmd == 4) {
                    taskManager.createEpic();
                } else if (cmd == 5) {
                    taskManager.updateEpic();
                } else if (cmd == 6) {
                    taskManager.removeEpic();
                } else if (cmd == 7) {
                    taskManager.removeAllEpics();
                } else if (cmd == 8) {
                    break;
                }
                }
            } else if (type == 3) {
                while(true) {
                    printMainMenu();
                    int cmd = scanner.nextInt();
                    scanner.nextLine();
                    if (cmd == 1) {
                        taskManager.printAllSubtasks();
                    } else if (cmd == 2) {
                        taskManager.printSubtask();
                    } else if (cmd == 3) {
                        taskManager.createSubtask();
                    } else if (cmd == 4) {
                        taskManager.updateSubtask();
                    } else if (cmd == 5) {
                        taskManager.removeSubtask();
                    } else if (cmd == 6) {
                        taskManager.removeAllSubtasks();
                    } else if (cmd == 7) {
                        break;
                    }
                }
            } else if (type == 4) {
                System.out.println("Работа завершена");
                return;
            } else {
                System.out.println("Некорректный тип");
            }
        }
    }

    static void printMainMenu() { //Для задач и подзадач
        System.out.println("Выберите действие:");
        System.out.println("1 - Вывести список задач");
        System.out.println("2 - Вывести задачу с указанным идентификатором");
        System.out.println("3 - Добавить задачу");
        System.out.println("4 - Обновить задачу");
        System.out.println("5 - Удалить задачу с указанным идентификатором");
        System.out.println("6 - Удалить все задачи данного типа");
        System.out.println("7 - Назад");
    }

    static void printEpicMainMenu() { //Для эпиков
        System.out.println("Выберите действие:");
        System.out.println("1 - Вывести список эпиков");
        System.out.println("2 - Вывести эпик с указанным идентификатором");
        System.out.println("3 - Вывести все подзадачи эпика с указанным идентификатором");
        System.out.println("4 - Добавить эпик");
        System.out.println("5 - Обновить эпик");
        System.out.println("6 - Удалить эпик с указанным идентификатором");
        System.out.println("7 - Удалить все эпики");
        System.out.println("8 - Назад");
    }

    static void printTypesMenu() {
        System.out.println("Выберите тип задачи:");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        System.out.println("4 - Выход");
    }
}
