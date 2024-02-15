package test.manager;

import manager.HistoryManager;
import manager.Managers;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static models.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @DisplayName("GIVEN a new simple Task " +
            "WHEN add task to historyManager AND get history " +
            "THEN a history is not null AND a size of history equals 1")
    @Test
    public void test1_getHistoryOneTask(){
        Task task = new Task();

        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @DisplayName("GIVEN a new three Task, two Epic, two Subtask " +
            "WHEN add Tasks " +
            "THEN historyManager add true order and unique")
    @Test
    public void test2_getHistoryFewTask() {
        // Дано (Given)
        Task task1 = new Task("Task1", NEW);
        task1.setId(1);
        Task task2 = new Task("Task2", NEW);
        task2.setId(2);
        Task task3 = new Task("Task3", NEW);
        task3.setId(3);

        Epic epic1 = new Epic("Epic1", "Epic description", NEW);
        epic1.setId(4);
        Subtask subtask11 = new Subtask("Subtask11", "Subtask description", NEW, epic1.getId());
        subtask11.setId(5);
        Subtask subtask12 = new Subtask("Subtask12", "Subtask description", NEW, epic1.getId());
        subtask12.setId(6);

        Epic epic2 = new Epic("Epic2", "Epic description", NEW);
        epic2.setId(7);

        // Совершаемое действие (When)
        historyManager.add(task2);
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subtask12);
        historyManager.add(epic1);
        historyManager.add(task3);
        historyManager.add(subtask11);
        historyManager.add(task2);
        historyManager.add(subtask12);

        // Проверки (Then)
        List<Task> expectList = List.of(task1, epic2, epic1, task3, subtask11, task2, subtask12);
        List<Task> getList = historyManager.getHistory();

        assertEquals(expectList.size(), getList.size());
        for (int i = 0; i < expectList.size(); i++) {
            assertEquals(expectList.get(i), getList.get(i));
        }
    }
}