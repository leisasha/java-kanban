package test.manager;

import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    public void shouldBeCreateTaskManager(){
        //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager);
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @Test
    public void shouldBeCreateHistoryManager(){
        //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }
}