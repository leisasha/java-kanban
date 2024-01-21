package test.models;

import manager.Managers;
import manager.TaskManager;
import models.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static models.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldBeEqualsEpic1AndEpic2WithSameId(){
        //проверьте, что наследники класса Task равны друг другу, если равен их id
        Epic task1 = new Epic("Task1", "Test Task1 description", NEW);
        taskManager.makeTask(task1);

        Epic task2 = new Epic("Task2", "Test Task2 description", NEW);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи не совпадают.");
    }
}