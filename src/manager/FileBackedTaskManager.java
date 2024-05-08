package manager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import models.*;

import exceptions.ManagerSaveException;
import exceptions.ManagerLoadException;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File saveFile;

    public FileBackedTaskManager(File saveFile) {
        super();
        this.saveFile = saveFile;
    }

    @Override
    public void makeTask(Task task) {
        super.makeTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (Reader fileReader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                final String line = bufferedReader.readLine();
                final Task task = taskfromString(line);
                if (task != null && task.getId() != 0) {
                    manager.getTasks().put(task.getId(), task);
                    if (manager.getCount() < task.getId()) {
                        manager.setCount(task.getId());
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new ManagerLoadException("Ошибка загрузки данных из файла: " + e.getMessage());
        }

        for (Task task : manager.getTasksList()) {
            if (task.getClass() == Subtask.class) {
                final Subtask subtask = (Subtask) task;
                final Epic epic = (Epic) manager.getTask(subtask.getEpicId());
                List<Integer> subtasksId = epic.getSubtasksId();
                subtasksId.add(subtask.getId());
            }
        }

        return manager;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(saveFile, StandardCharsets.UTF_8)) {
            final String csvData = generateCSVData();
            fileWriter.write(csvData);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задачи в файл: " + e.getMessage());
        }
    }

    private static Task taskfromString(String value) throws NumberFormatException {
        if (value.trim().isEmpty()) {
            return null;
        }

        Task task;
        final String[] piecesOfLine = value.split(",");

        final int id = Integer.parseInt(piecesOfLine[0].trim());
        final String name = piecesOfLine[2];
        final Status status = Status.valueOf(piecesOfLine[3]);
        final String description = piecesOfLine[4];

        if (TaskTypes.EPIC == TaskTypes.valueOf(piecesOfLine[1])) {
            task = new Epic(name, description, status);
        } else if (TaskTypes.SUBTASK == TaskTypes.valueOf(piecesOfLine[1])) {
            final int epicId = Integer.parseInt(piecesOfLine[5].trim());
            task = new Subtask(name, description, status, epicId);
        } else {
            task = new Task(name, description, status);
        }
        task.setId(id);

        return task;
    }

    private String generateCSVData() {
        StringBuilder csvData = new StringBuilder();
        csvData.append("id,type,name,status,description,epic").append(System.lineSeparator());

        for (Task task : getTasksList()) {
            csvData.append(taskToString(task)).append(System.lineSeparator());
        }

        return csvData.toString();
    }

    private String taskToString(Task task) {
        String taskTypes = "";
        String epic = "";

        if (task.getClass() == Task.class) {
            taskTypes = TaskTypes.TASK.toString();
        } else if (task.getClass() == Epic.class) {
            taskTypes = TaskTypes.EPIC.toString();
        } else if (task.getClass() == Subtask.class) {
            taskTypes = TaskTypes.SUBTASK.toString();
            epic = Integer.toString(((Subtask) task).getEpicId());
        }

        return task.getId() + "," +
                taskTypes + "," +
                task.getName() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                epic;
    }
}
