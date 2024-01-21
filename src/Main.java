import manager.TaskManager;
import manager.InMemoryTaskManager;
import models.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        int count = 0;
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        goingTestTask1(inMemoryTaskManager, count);
        goingTestTask2(inMemoryTaskManager, count);
        goingTestEpicWithTwoSubtask(inMemoryTaskManager, count);
        goingTestEpicWithOneSubtask(inMemoryTaskManager, count);
        goingTestDeleteAll(inMemoryTaskManager, count);
    }

    public static void goingTestTask1(InMemoryTaskManager inMemoryTaskManager, int count) {
        Task task = new Task("task1", Status.NEW);

        inMemoryTaskManager.makeTask(task);
        inMemoryTaskManager.makeTask(task); //дважды не создается одна таска, а обновляется

        System.out.println("Task1");
        System.out.println("№" + (++count) + " " + task); //№1 Task{id=1, name='task1', description=null, status=NEW}
        System.out.println();
    }

    public static void goingTestTask2(InMemoryTaskManager inMemoryTaskManager, int count) {
        Task task = new Task("task2", "Задача номер 2", Status.NEW);

        inMemoryTaskManager.makeTask(task);

        System.out.println("Task2");
        System.out.println("№" + (++count) + " " + task); //№1 Task{id=2, name='task2', description.length=14, status=NEW}

        task.setStatus(Status.DONE);

        System.out.println("№" + (++count) + " " + task); //№2 Task{id=2, name='task2', description.length=14, status=DONE}
        System.out.println();
    }

    public static void goingTestEpicWithTwoSubtask(InMemoryTaskManager inMemoryTaskManager, int count) {
        System.out.println("Epic with two Subtask");

        Epic epic = new Epic();
        epic.setName("epic1");

        inMemoryTaskManager.makeTask(epic);

        System.out.println("№" + (++count) + " " + epic); //№1 Epic{id=3, name='epic1', description=null, status=NEW}

        Subtask subtask1 = new Subtask(epic.getId());
        inMemoryTaskManager.makeTask(subtask1);
        System.out.println("№" + (++count) + " " + subtask1); //№2 Subtask{id=4, name='null', description=null, status=NEW}

        Subtask subtask2 = new Subtask(epic.getId());
        inMemoryTaskManager.makeTask(subtask2);
        inMemoryTaskManager.makeTask(subtask2);
        System.out.println("№" + (++count) + " " + subtask2); //№3 Subtask{id=5, name='null', description=null, status=NEW}

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(epic); //управление статусами происходит в менеджере (из задания)
        System.out.println("№" + (++count) + " " + epic); //№4 Epic{id=3, name='epic1', description=null, status=IN_PROGRESS}

        epic.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(epic); //Для Epic статус всегда расчитывается (из задания), поэтому статус не изменится
        System.out.println("№" + (++count) + " " + epic); //№5 Epic{id=3, name='epic1', description=null, status=IN_PROGRESS}

        ArrayList<Subtask> subtasks = inMemoryTaskManager.getWholeSubtasks(epic); // получаем список подзадач
        for (Subtask subtask : subtasks) {
            /*
            №6 Subtask{id=4, name='null', description=null, status=DONE}
            №7 Subtask{id=5, name='null', description=null, status=IN_PROGRESS}
            */
            System.out.println("№" + (++count) + " " + subtask);
        }

        inMemoryTaskManager.removeTask(5); // Удалим одну подзадачу у epic1
        subtasks = inMemoryTaskManager.getWholeSubtasks(epic); // получаем список подзадач
        for (Subtask subtask : subtasks) {
            /*
            №8 Subtask{id=4, name='null', description=null, status=DONE}
            */
            System.out.println("№" + (++count) + " " + subtask);
        }
        System.out.println();
        // Epic with two Subtask --
    }

    public static void goingTestEpicWithOneSubtask(InMemoryTaskManager inMemoryTaskManager, int count) {
        System.out.println("Epic with one Subtask");

        Epic epic = new Epic();
        epic.setName("epic2");

        inMemoryTaskManager.makeTask(epic);

        System.out.println("№" + (++count) + " " + epic); //№1 Epic{id=6, name='epic2', description=null, status=NEW}

        Subtask subtask = new Subtask(epic.getId());
        inMemoryTaskManager.makeTask(subtask);
        System.out.println("№" + (++count) + " " + subtask); //№2 Subtask{id=7, name='null', description=null, status=NEW}

        subtask.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(epic);
        System.out.println("№" + (++count) + " " + epic); //№3 Epic{id=6, name='epic2', description=null, status=DONE}
        System.out.println();
    }

    public static void goingTestDeleteAll(InMemoryTaskManager inMemoryTaskManager, int count) {
        System.out.println("Прочие тесты со всеми Task, Epic и Subtask");

        ArrayList<Task> tasks = inMemoryTaskManager.getTasksList();
        for (Task task : tasks) {
            System.out.println("№" + (++count) + " " + task);
        }

        inMemoryTaskManager.removeTasks(); // Удаляем все задачи

        tasks = inMemoryTaskManager.getTasksList();
        for (Task task : tasks) {
            System.out.println("№" + (++count) + " " + task);
        }
    }
}
