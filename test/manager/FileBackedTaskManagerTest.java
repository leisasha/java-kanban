package manager;

import models.Status;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    public void beforeEach() {
        try {
            tempFile = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
        super.manager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @Test
    public void loadEmptyFileShouldReturnEmptyManager() {
        assertEquals(manager.getTasksList().size(), 0);
    }

    @Test
    public void saveEmptyManagerToFileShouldBeEmptyFile() {
        manager.save();
        assertEquals(manager.getTasksList().size(), FileBackedTaskManager.loadFromFile(tempFile).getTasksList().size());
    }

    @Test
    public void saveAndLoadSomeTasksToFile() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.DONE);
        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.NEW);

        manager.makeTask(task1);
        manager.makeTask(task2);
        manager.makeTask(task3);

        assertEquals(manager.getTasksList().size(), FileBackedTaskManager.loadFromFile(tempFile).getTasksList().size());
    }

    @Test
    @Override
    public void allSubtasksWithStatusNewAndDone() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(manager.getTasksList().size(), loadedManager.getTasksList().size());

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