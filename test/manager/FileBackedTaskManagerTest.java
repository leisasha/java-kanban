package manager;

import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;
    private FileBackedTaskManager loadedManager;

    @BeforeEach
    public void beforeEach() {
        try {
            tempFile = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test
    public void loadEmptyFileShouldReturnEmptyManager(){
        loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(loadedManager.getTasksList().size(), 0);
    }

    @Test
    public void saveEmptyManagerToFileShouldBeEmptyFile(){
        manager = new FileBackedTaskManager(tempFile);
        manager.save();
        loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(loadedManager.getTasksList().size(), manager.getTasksList().size());
        assertEquals(loadedManager.getTasksList().size(), manager.getTasksList().size());
    }

    @Test
    public void saveAndLoadSomeTasksToFile() {
        manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.DONE);
        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.NEW);

        manager.makeTask(task1);
        manager.makeTask(task2);
        manager.makeTask(task3);

        loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(manager.getTasksList().size(), loadedManager.getTasksList().size());
    }

    @Test
    public void createAndCompareTasksShouldBeEqualsSameTasks() {
        manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.IN_PROGRESS);
        manager.makeTask(task1);

        Task epic1 = new Epic("Задача 2", "Описание задачи 2", Status.NEW);
        manager.makeTask(epic1);

        Task epic2 = new Epic("Задача 3", "Описание задачи 3", Status.NEW);
        manager.makeTask(epic2);

        Task subtask21 = new Subtask("Задача 4", "Описание задачи 4", Status.DONE, epic2.getId());
        manager.makeTask(subtask21);

        Task subtask22 = new Subtask("Задача 5", "Описание задачи 5", Status.NEW, epic2.getId());
        manager.makeTask(subtask22);

        loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(manager.getTasksList().size(), loadedManager.getTasksList().size());

        // Проверка соответствия загруженных задач созданным
        List<Task> originalTasks = new ArrayList<>(manager.getTasksList());
        List<Task> loadedTasks = new ArrayList<>(loadedManager.getTasksList());
        for (int i = 0; i < originalTasks.size(); i++) {
            Task originalTask = originalTasks.get(i);
            Task loadedTask = loadedTasks.get(i);
            assertEquals(originalTask.getId(), loadedTask.getId());
            assertEquals(originalTask.getName(), loadedTask.getName());
            assertEquals(originalTask.getStatus(), loadedTask.getStatus());
        }
    }
}