package managers;

import history.HistoryManager;
import history.InMemoryHistoryManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.home"), "testFile.txt"));
        //return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
