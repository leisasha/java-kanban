package manager;

import exceptions.ManagerValidationException;
import exceptions.NotFoundException;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int count = 0;
    private Map<Integer, Task> tasks = new HashMap<>();

    @Override
    public void makeTask(Task task) {
        if (task.getId() == 0) {
            task.setId(++count);

            if (task.getClass() == Subtask.class) {
                Subtask subtask = (Subtask) task;
                Epic epic = (Epic) tasks.get(subtask.getEpicId());
                if (epic != null)
                    epic.getSubtasksId().add(subtask.getId());
                else
                    subtask.setEpicId(0);
            }
        }
        updateTask(task);
    }

    @Override
    public void updateTask(Task task) {
        validateTask(task);
        tasks.put(task.getId(), task);
        refreshStatus(task);
        refreshDateTime(task);
        addPrioritizedTasks(task);
    }

    @Override
    public Task getTask(int id) throws NotFoundException {
        Task resultFunction = tasks.get(id);

        if (resultFunction != null)
            historyManager.add(resultFunction);
        else
            throw new NotFoundException("Ошибка получения задачи: Пустая ссылка");

        return resultFunction;
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.get(id);
        if (task.getClass() == Epic.class) {
            Epic epic = (Epic) task;
            epic.getSubtasksId().forEach(subtaskId -> tasks.remove(subtaskId));
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            epic.getSubtasksId().remove((Integer) id);
            refreshEpicStatus(epic);
            refreshEpicDateTime(epic);
        }
        tasks.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    @Override
    public void removeTasks() {
        tasks.clear();
        prioritizedTasks.clear();
        count = 0;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getWholeSubtasks(Epic epic) {
        return epic.getSubtasksId().stream()
                .map(subtaskId -> (Subtask) tasks.get(subtaskId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }


    private void refreshEpicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isWholeNew = epic.getSubtasksId().stream()
                    .map(id -> (Subtask) tasks.get(id))
                    .allMatch(subtask -> subtask.getStatus() == Status.NEW);

            boolean isWholeDone = epic.getSubtasksId().stream()
                    .map(id -> (Subtask) tasks.get(id))
                    .allMatch(subtask -> subtask.getStatus() == Status.DONE);

            if (isWholeNew) {
                epic.setStatus(Status.NEW);
            } else if (isWholeDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    private void refreshStatus(Task task) {
        if (task.getClass() == Epic.class) {
            refreshEpicStatus((Epic) task);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            if (epic != null)
                refreshEpicStatus(epic);
        }
    }

    private void refreshEpicDateTime(Epic epic) {
        if (!epic.getSubtasksId().isEmpty()) {
            final Subtask minSubtask = (Subtask) epic.getSubtasksId().stream()
                    .map(this::getTask)
                    .min(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(ZonedDateTime::compareTo)))
                    .orElseThrow();
            epic.setStartTime(minSubtask.getStartTime());

            final Subtask maxSubtask = (Subtask) epic.getSubtasksId().stream()
                    .map(this::getTask)
                    .max(Comparator.comparing(Task::getEndTime, Comparator.nullsFirst(ZonedDateTime::compareTo)))
                    .orElseThrow();
            epic.setEndTime(maxSubtask.getStartTime());

            if (Optional.ofNullable(epic.getStartTime()).isPresent()
                    && Optional.ofNullable(epic.getEndTime()).isPresent()) {
                epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
            } else {
                epic.setDuration(null);
            }
        } else {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
        }
    }

    private void refreshDateTime(Task task) {
        if (task.getClass() == Epic.class) {
            refreshEpicDateTime((Epic) task);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            if (epic != null)
                refreshEpicDateTime(epic);
        }
    }

    private void addPrioritizedTasks(Task task) {
        if (Optional.ofNullable(task.getStartTime()).isPresent()) {
            prioritizedTasks.add(task);
        }
    }

    private void validateTask(Task task) throws ManagerValidationException {
        final List<Task> list = getPrioritizedTasks();

        final boolean isCovered = list.stream()
                .anyMatch(taskFromStream -> isDateTimeCoverTwoTasks(task, taskFromStream));

        if (isCovered) {
            throw new ManagerValidationException("Ошибка. Задачи пересекаются по времени выполнения: " + task);
        }
    }

    private boolean isDateTimeCoverTwoTasks(Task task1, Task task2) {
        if (Optional.ofNullable(task1.getStartTime()).isEmpty()
                || Optional.ofNullable(task1.getEndTime()).isEmpty()
                || Optional.ofNullable(task2.getStartTime()).isEmpty()
                || Optional.ofNullable(task2.getEndTime()).isEmpty()) {
            return false;
        }

        long start1 = task1.getStartTime().toInstant().toEpochMilli();
        long end1 = task1.getEndTime().toInstant().toEpochMilli();
        long start2 = task2.getStartTime().toInstant().toEpochMilli();
        long end2 = task2.getEndTime().toInstant().toEpochMilli();

        return (start1 - end2) * (start2 - end1) > 0;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(Map<Integer, Task> tasks) {
        this.tasks = tasks;
    }
}
