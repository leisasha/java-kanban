package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File saveFile;

    public FileBackedTaskManager(File saveFile) {
        super();
        this.saveFile = saveFile;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (Reader fileReader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            bufferedReader.readLine();
            bufferedReader.lines()
                    .map(FileBackedTaskManager::taskfromString)
                    .filter(task -> task != null && task.getId() != 0)
                    .forEach(task -> {
                        manager.getTasks().put(task.getId(), task);
                        if (manager.getCount() < task.getId()) {
                            manager.setCount(task.getId());
                        }
                    });
        } catch (IOException | NumberFormatException e) {
            throw new ManagerLoadException("Ошибка загрузки данных из файла: " + e.getMessage());
        }

        manager.getTasksList().stream()
                .filter(task -> task.getClass() == Subtask.class)
                .map(task -> (Subtask) task)
                .forEach(subtask -> {
                    final Epic epic = (Epic) manager.getTask(subtask.getEpicId());
                    epic.getSubtasksId().add(subtask.getId());
                });

        return manager;
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
        final String description = piecesOfLineValidate(piecesOfLine, 4) ? piecesOfLine[4] : "";
        final ZonedDateTime startTime = piecesOfLineValidate(piecesOfLine, 6)
                ? ZonedDateTime.parse(piecesOfLine[6]) : null;
        final Duration duration = piecesOfLineValidate(piecesOfLine, 7)
                ? Duration.ofMinutes(Integer.parseInt(piecesOfLine[7].trim())) : null;
        final ZonedDateTime endTime = piecesOfLineValidate(piecesOfLine, 8)
                ? ZonedDateTime.parse(piecesOfLine[8]) : null;

        if (TaskTypes.EPIC == TaskTypes.valueOf(piecesOfLine[1])) {
            Epic epic = new Epic(name, description, status);
            epic.setEndTime(endTime);
            task = (Task) epic;
        } else if (TaskTypes.SUBTASK == TaskTypes.valueOf(piecesOfLine[1])) {
            final int epicId = Integer.parseInt(piecesOfLine[5].trim());
            task = new Subtask(name, description, status, epicId);
        } else {
            task = new Task(name, description, status);
        }
        task.setId(id);
        task.setStartTime(startTime);
        task.setDuration(duration);

        return task;
    }

    private static boolean piecesOfLineValidate(String[] piecesOfLine, int i) {
        return piecesOfLine.length > i && !piecesOfLine[i].trim().isEmpty();
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

    public void save() {
        try (Writer fileWriter = new FileWriter(saveFile, StandardCharsets.UTF_8)) {
            final String csvData = generateCSVData();
            fileWriter.write(csvData);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задачи в файл: " + e.getMessage());
        }
    }

    private String generateCSVData() {
        StringBuilder csvData = new StringBuilder();
        csvData.append("id,type,name,status,description,epic,startTime,duration,endTime").append(System.lineSeparator());

        getTasksList().forEach(task -> csvData.append(taskToString(task)).append(System.lineSeparator()));

        return csvData.toString();
    }

    private String taskToString(Task task) {
        String taskTypes = "";
        String epic = "";
        String startTime = "";
        String duration = "";
        String endTime = "";
        if (Optional.ofNullable(task.getStartTime()).isPresent()) {
            startTime = task.getStartTime().toString();
        }
        if (Optional.ofNullable(task.getDuration()).isPresent()) {
            duration = String.valueOf(task.getDuration().toMinutes());
        }
        if (Optional.ofNullable(task.getEndTime()).isPresent()) {
            endTime = task.getEndTime().toString();
        }

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
                epic + "," +
                startTime + "," +
                duration + "," +
                endTime;
    }
}
