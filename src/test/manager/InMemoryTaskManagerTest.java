package test.manager;

import manager.Managers;
import models.Epic;
import models.Subtask;
import models.Task;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static models.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void test1_creationTaskShouldBeNotNullAndEqualsById(){
        Task task = new Task("Task", "Task description", NEW);
        taskManager.makeTask(task);

        final Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void test2_creationEpicShouldBeNotNullAndEqualsById(){
        Epic epic = new Epic("Epic", "Epic description", NEW);
        taskManager.makeTask(epic);

        final Task savedTask = taskManager.getTask(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void test3_creationSubtaskShouldBeNotNullAndEqualsById(){
        Epic epic = new Epic("Epic", "Epic description", NEW);
        taskManager.makeTask(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        taskManager.makeTask(subtask);

        final Task savedTask = taskManager.getTask(subtask.getId());

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test void test4_getNullTaskShouldBeNullExpect() {
        Task task = taskManager.getTask(1);
        assertNull(task);
    }

    @DisplayName("GIVEN a new two Epic, three Subtask " +
            "WHEN removeTask Epic2 " +
            "THEN getWholeSubtasks(epic1) is Positive AND getWholeSubtasks(epic2) is Negative")
    @Test
    public void test5_getWholeSubtasksAndRemove() {
        // Дано (Given)
        Epic epic1 = new Epic("Epic1", "Epic description", NEW);
        taskManager.makeTask(epic1);

        Subtask subtask11 = new Subtask("Subtask11", "Subtask description", NEW, epic1.getId());
        taskManager.makeTask(subtask11);

        Subtask subtask12 = new Subtask("Subtask12", "Subtask description", NEW, epic1.getId());
        taskManager.makeTask(subtask12);

        Epic epic2 = new Epic("Epic2", "Epic description", NEW);
        taskManager.makeTask(epic2);

        Subtask subtask21 = new Subtask("Subtask21", "Subtask description", NEW, epic2.getId());
        taskManager.makeTask(subtask21);

        // Совершаемое действие (When)
        taskManager.removeTask(epic2.getId());

        // Проверки (Then)
        List<? extends Task> getListPositive = taskManager.getWholeSubtasks(epic1);
        assertEquals(2, getListPositive.size());

        List<? extends Task> getListNegative = taskManager.getWholeSubtasks(epic2);
        assertEquals(0, getListNegative.size());
    }

    @DisplayName("GIVEN a new Task that added to taskManager " +
            "WHEN use taskManager.getTask() AND get history " +
            "THEN a history is not null AND a size of history equals 1")
    @Test
    public void test6_getHistoryOneTask() {
        Task task = new Task("Task", NEW);
        taskManager.makeTask(task);

        taskManager.getTask(task.getId());
        List<Task> history = taskManager.getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @DisplayName("GIVEN a new three Task, two Epic, two Subtask " +
            "WHEN use taskManager.getTask() " +
            "THEN a history size is 5 AND history item equals to expect item")
    @Test
    public void test7_getHistoryFewTaskValidationHistory() {
        // Дано (Given)
        Task task1 = new Task("Task1", NEW);
        taskManager.makeTask(task1);

        Task task2 = new Task("Task2", NEW);
        taskManager.makeTask(task2);

        Task task3 = new Task("Task3", NEW);
        taskManager.makeTask(task3);

        Epic epic1 = new Epic("Epic1", "Epic description", NEW);
        taskManager.makeTask(epic1);

        Subtask subtask11 = new Subtask("Subtask11", "Subtask description", NEW, epic1.getId());
        taskManager.makeTask(subtask11);

        Subtask subtask12 = new Subtask("Subtask12", "Subtask description", NEW, epic1.getId());
        taskManager.makeTask(subtask12);

        Epic epic2 = new Epic("Epic2", "Epic description", NEW);
        taskManager.makeTask(epic2);

        // Совершаемое действие (When)
        taskManager.getTask(epic2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(subtask11.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(epic2.getId());

        // Проверки (Then)
        List<Task> expectList = List.of(task1, subtask11, task2, task3, epic2);
        List<Task> getList = taskManager.getHistory();

        assertEquals(expectList.size(), getList.size());
        for (int i = 0; i < expectList.size(); i++) {
            assertEquals(expectList.get(i), getList.get(i));
        }
    }

    @DisplayName("GIVEN a new three Task, two Epic, two Subtask " +
            "WHEN use taskManager.getTask() AND remove Epic with two Subtasks" +
            "THEN a history size is 3 AND history item equals to expect item")
    @Test
    public void test8_removeTaskValidationHistory() {
        // Дано (Given)
        Task task1 = new Task("Task1", NEW);
        taskManager.makeTask(task1);

        Task task2 = new Task("Task2", NEW);
        taskManager.makeTask(task2);

        Task task3 = new Task("Task3", NEW);
        taskManager.makeTask(task3);

        Epic epic1 = new Epic("Epic1", "Epic description", NEW);
        taskManager.makeTask(epic1);

        Subtask subtask11 = new Subtask("Subtask11", "Subtask description", NEW, epic1.getId());
        taskManager.makeTask(subtask11);

        Subtask subtask12 = new Subtask("Subtask12", "Subtask description", NEW, epic1.getId());
        taskManager.makeTask(subtask12);

        Epic epic2 = new Epic("Epic2", "Epic description", NEW);
        taskManager.makeTask(epic2);

        // Совершаемое действие (When)
        taskManager.getTask(epic1.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(subtask11.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(epic1.getId());

        taskManager.removeTask(epic1.getId());

        // Проверки (Then)
        List<Task> expectList = List.of(task1, task2, task3);
        List<Task> getList = taskManager.getHistory();

        assertEquals(expectList.size(), getList.size());
        for (int i = 0; i < expectList.size(); i++) {
            assertEquals(expectList.get(i), getList.get(i));
        }
    }
}