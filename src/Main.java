import tasktracker.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        /*Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.*/

        int count = 0;

        // Task1 ++
        System.out.println("Task1");
        Task task1 = new Task("task1", Status.NEW);
        TaskManager.makeTask(task1);
        TaskManager.makeTask(task1); //дважды не создается одна таска, а обновляется
        System.out.println("№" + (++count) + " " + task1); //№1 Task{id=1, name='task1', description=null, status=NEW}
        System.out.println();
        // Task1 --


        // Task2 ++
        System.out.println("Task2");
        Task task2 = new Task("task2", "Задача номер 2", Status.NEW);
        TaskManager.makeTask(task2);
        task2.setStatus(Status.DONE);
        System.out.println("№" + (++count) + " " + task2); //№2 Task{id=2, name='task2', description.length=14, status=DONE}
        System.out.println();
        // Task2 --


        // Epic with two Subtask ++
        System.out.println("Epic with two Subtask");

        Epic epic1 = new Epic();
        epic1.setName("epic1");
        TaskManager.makeTask(epic1);
        System.out.println("№" + (++count) + " " + epic1); //№3 Epic{id=3, name='epic1', description=null, status=NEW}

        Subtask subtask11 = new Subtask(epic1.getId());
        TaskManager.makeTask(subtask11);
        System.out.println("№" + (++count) + " " + subtask11); //№4 Subtask{id=4, name='null', description=null, status=NEW}

        Subtask subtask21 = new Subtask(epic1.getId());
        TaskManager.makeTask(subtask21);
        TaskManager.makeTask(subtask21);
        System.out.println("№" + (++count) + " " + subtask21); //№5 Subtask{id=5, name='null', description=null, status=NEW}

        subtask11.setStatus(Status.DONE);
        subtask21.setStatus(Status.IN_PROGRESS);
        TaskManager.updateTask(epic1); //управление статусами происходит в менеджере (из задания)
        System.out.println("№" + (++count) + " " + epic1); //№6 Epic{id=3, name='epic1', description=null, status=IN_PROGRESS}

        epic1.setStatus(Status.DONE);
        TaskManager.updateTask(epic1); //Для Epic статус всегда расчитывается (из задания), поэтому статус не изменится
        System.out.println("№" + (++count) + " " + epic1); //№7 Epic{id=3, name='epic1', description=null, status=IN_PROGRESS}

        ArrayList<Subtask> subtasks = TaskManager.getWholeSubtasks(epic1); // получаем список подзадач
        for (Subtask subtask : subtasks) {
            /*
            №8 Subtask{id=4, name='null', description=null, status=DONE}
            №9 Subtask{id=5, name='null', description=null, status=IN_PROGRESS}
            */
            System.out.println("№" + (++count) + " " + subtask);
        }

        TaskManager.removeTask(5); // Удалим одну подзадачу у epic1
        subtasks = TaskManager.getWholeSubtasks(epic1); // получаем список подзадач
        for (Subtask subtask : subtasks) {
            /*
            №10 Subtask{id=5, name='null', description=null, status=IN_PROGRESS}
            */
            System.out.println("№" + (++count) + " " + subtask);
        }
        System.out.println();
        // Epic with two Subtask --


        // Epic with one Subtask ++
        System.out.println("Epic with one Subtask");

        Epic epic2 = new Epic();
        epic2.setName("epic2");
        TaskManager.makeTask(epic2);
        System.out.println("№" + (++count) + " " + epic2); //№11 Epic{id=6, name='epic2', description=null, status=NEW}

        Subtask subtask12 = new Subtask(epic2.getId());
        TaskManager.makeTask(subtask12);
        System.out.println("№" + (++count) + " " + subtask12); //№12 Subtask{id=7, name='null', description=null, status=NEW}

        subtask12.setStatus(Status.DONE);
        TaskManager.updateTask(epic2);
        System.out.println("№" + (++count) + " " + epic2); //№13 Epic{id=6, name='epic2', description=null, status=DONE}
        System.out.println();
        // Epic with one Subtask --


        // Прочие тесты со всеми Task, Epic и Subtask ++
        System.out.println("Прочие тесты со всеми Task, Epic и Subtask");
        ArrayList<Task> tasks = TaskManager.getTasksList();
        for (Task task : tasks) {
            System.out.println("№" + (++count) + " " + task);
        }

        TaskManager.removeTasks(); // Удаляем все задачи

        tasks = TaskManager.getTasksList();
        for (Task task : tasks) {
            System.out.println("№" + (++count) + " " + task);
        }
        // Прочие тесты со всеми Task, Epic и Subtask --
    }
}
