package models;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldBeNegativeWhenSubtaskSetEpicYourself() {
        Subtask subtask = new Subtask("Subtask", "Test Subtask description", 10);
        taskManager.makeTask(subtask);

        assertNotEquals(10, subtask.getEpicId());
    }
}