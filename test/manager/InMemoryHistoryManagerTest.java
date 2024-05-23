package manager;

import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static models.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldEmptyHistoryManager() {
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void addTaskShouldNotEmptyHistoryManager() {
        Task task = new Task();
        historyManager.add(task);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void addTaskShouldDoublesHistoryManager() {
        Task task1 = new Task("Task1", "description", NEW);
        historyManager.add(task1);
        historyManager.add(task1);

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        history.forEach(task -> assertEquals(0, task.getId()));
    }
}