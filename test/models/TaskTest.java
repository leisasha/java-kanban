package models;

import manager.Managers;
import manager.TaskManager;

import static models.Status.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldBeEqualsTask1AndTask2WithSameId() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskManager.makeTask(task1);

        Task task2 = new Task("Test addNewTask", "Test addNewTask description", NEW);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}