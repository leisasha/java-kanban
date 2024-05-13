package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import static models.Status.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldWillAddTaskAndReturnId() {
        Task task = new Task("Task", "Task description", NEW);
        taskManager.makeTask(task);

        final Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void shouldWillAddEpicAndReturnId() {
        Epic epic = new Epic("Epic", "Epic description", NEW);
        taskManager.makeTask(epic);

        final Task savedTask = taskManager.getTask(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void shouldWillAddSubtaskAndReturnId() {
        Epic epic = new Epic("Epic", "Epic description", NEW);
        taskManager.makeTask(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask description", NEW, epic.getId());
        taskManager.makeTask(subtask);

        final Task savedTask = taskManager.getTask(subtask.getId());

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }
}