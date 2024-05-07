package manager;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int maxSizeList = 10;
    private final int oldestTask = 0;
    private final List<Task> historyTaskList = new ArrayList<>();

    public void add(Task task) {
        if (historyTaskList.size() >= maxSizeList) {
            historyTaskList.remove(oldestTask);
        }

        historyTaskList.add(task);
    }

    public List<Task> getHistory() {
        return historyTaskList;
    }
}
