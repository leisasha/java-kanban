package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {
    @Test
    public void shouldBeCreateTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @Test
    public void shouldBeCreateHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }
}