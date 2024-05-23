package manager;

import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static models.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @Test
    public void makeTaskShouldAddId() {
        Task task = new Task("Task", "description", NEW);
        manager.makeTask(task);
        final Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void makeEpicShouldAddId() {
        Epic epic = new Epic("Epic", "description", NEW);
        manager.makeTask(epic);
        final Task savedTask = manager.getTask(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void makeSubtaskShouldAddId() {
        Epic epic = new Epic("Epic", "description", NEW);
        manager.makeTask(epic);
        Subtask subtask = new Subtask("Subtask1", "description", NEW, epic.getId());
        manager.makeTask(subtask);
        final Task savedTask = manager.getTask(subtask.getId());

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void updateTaskShouldChangeStatus() {
        Task task = new Task("Task", "description", NEW);
        manager.makeTask(task);
        task.setStatus(IN_PROGRESS);
        manager.updateTask(task);
        final Task savedTask = manager.getTask(task.getId());

        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(IN_PROGRESS, task.getStatus());
    }

    @Test
    public void updateEpicShouldNotChangeStatus() {
        Epic epic = new Epic("Epic", "description", NEW);
        manager.makeTask(epic);
        epic.setStatus(IN_PROGRESS);
        manager.updateTask(epic);
        final Task savedTask = manager.getTask(epic.getId());

        assertEquals(epic, savedTask, "Задачи не совпадают.");
        assertEquals(NEW, epic.getStatus());
    }

    @Test
    public void updateEpicAndSubtaskShouldChangeStatus() {
        Epic epic = new Epic("Epic", "description", NEW);
        manager.makeTask(epic);
        Subtask subtask = new Subtask("Subtask1", "description", NEW, epic.getId());
        manager.makeTask(subtask);
        subtask.setStatus(IN_PROGRESS);
        manager.updateTask(subtask);
        final Task savedTask = manager.getTask(subtask.getId());

        assertEquals(subtask, savedTask, "Задачи не совпадают.");
        assertEquals(IN_PROGRESS, epic.getStatus());
        assertEquals(IN_PROGRESS, subtask.getStatus());
    }

    @Test
    public void allSubtasksWithStatusNew() {
        Epic epic = new Epic("Epic", "description", NEW);
        manager.makeTask(epic);
        manager.makeTask(new Subtask("Subtask1", "description", NEW, epic.getId()));
        manager.makeTask(new Subtask("Subtask2", "description", NEW, epic.getId()));
        List<Task> tasksList = new ArrayList<>(manager.getTasksList());

        assertEquals(3, manager.getTasksList().size());
        tasksList.forEach(task -> assertEquals(NEW, task.getStatus()));
    }

    @Test
    public void allSubtasksWithStatusDone() {
        Epic epic = new Epic("Epic", "description", NEW);
        manager.makeTask(epic);
        manager.makeTask(new Subtask("Subtask1", "description", DONE, epic.getId()));
        manager.makeTask(new Subtask("Subtask2", "description", DONE, epic.getId()));
        List<Task> tasksList = new ArrayList<>(manager.getTasksList());

        assertEquals(3, manager.getTasksList().size());
        tasksList.forEach(task -> assertEquals(DONE, task.getStatus()));
    }

    @Test
    public void allSubtasksWithStatusNewAndDone() {
        Task epic = new Epic("Epic", "description", NEW);
        manager.makeTask(epic);
        manager.makeTask(new Subtask("Subtask1", "description", NEW, epic.getId()));
        manager.makeTask(new Subtask("Subtask2", "description", DONE, epic.getId()));
        List<Task> tasksList = new ArrayList<>(manager.getTasksList());

        assertEquals(3, manager.getTasksList().size());
        tasksList.stream()
                .filter(task -> task.getClass() == Epic.class)
                .forEach(task -> assertEquals(IN_PROGRESS, task.getStatus()));
    }

    @Test
    public void allSubtasksWithStatusInProgress() {
        Epic epic = new Epic("Epic", "description", NEW);
        manager.makeTask(epic);
        manager.makeTask(new Subtask("Subtask1", "description", IN_PROGRESS, epic.getId()));
        manager.makeTask(new Subtask("Subtask2", "description", IN_PROGRESS, epic.getId()));
        List<Task> tasksList = new ArrayList<>(manager.getTasksList());

        assertEquals(3, manager.getTasksList().size());
        tasksList.forEach(task -> assertEquals(IN_PROGRESS, task.getStatus()));
    }
}
