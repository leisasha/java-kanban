package test.models;

import manager.Managers;
import manager.TaskManager;
import models.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldBeNegativeWhenSubtaskSetEpicYourself(){
        //проверьте, что объект Subtask нельзя сделать своим же эпиком
        Subtask subtask = new Subtask("Subtask", "Test Subtask description", 10);
        taskManager.makeTask(subtask);

        assertNotEquals(10, subtask.getEpicId());
    }
}