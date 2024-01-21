package manager;

import models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    final int MAX_SIZE_LIST = 10;
    final int OLDEST_TASK = 0;
    private final ArrayList<Task> historyTaskList = new ArrayList<>();

    public void add(Task task) {
        if (historyTaskList.size() >= MAX_SIZE_LIST) {
            historyTaskList.remove(OLDEST_TASK);
        }

        historyTaskList.add(task);
    }

    public ArrayList<Task> getHistory() {
        return historyTaskList;
    }
}
