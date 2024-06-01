package manager;

import java.io.File;
import java.io.IOException;

public class Managers {
    public static TaskManager getDefault() {
        File tempFile = null;

        try {
            tempFile = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FileBackedTaskManager(tempFile);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
