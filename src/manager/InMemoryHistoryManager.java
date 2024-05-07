package manager;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int MAXSIZELIST = 10;
    private final int OLDESTTASK = 0;
    private final List<Task> historyTaskList = new ArrayList<>();

    public void add(Task task) {
        if (historyTaskList.size() >= MAXSIZELIST) {
            historyTaskList.remove(OLDESTTASK);
        }

        historyTaskList.add(task);
    }

    public List<Task> getHistory() {
        return historyTaskList;
    }
}
