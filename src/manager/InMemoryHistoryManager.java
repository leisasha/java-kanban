package manager;

import models.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final int MAX_SIZE_LIST = 10;
    private final int OLDEST_TASK = 0;
    private final List<Task> historyTaskList = new ArrayList<>();

    public void add(Task task) {
        if (historyTaskList.size() >= MAX_SIZE_LIST) {
            historyTaskList.remove(OLDEST_TASK);
        }

        historyTaskList.add(task);
    }

    public List<Task> getHistory() {
        return historyTaskList;
    }
}
