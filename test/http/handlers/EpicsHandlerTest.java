package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.taskServer.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import models.adapters.DurationAdapter;
import models.adapters.ZonedDateTimeAdapter;
import models.typeTokens.EpicListTypeToken;
import models.typeTokens.SubtaskListTypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static models.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

public class EpicsHandlerTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .create();

    @BeforeEach
    public void setUp() {
        manager.removeTasks();
        try {
            taskServer.startServer();
        } catch (IOException e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void makeEpicShouldAddOneEpicWith201() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics");
        Epic epic = new Epic("Epic", "description", NEW);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasksList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void getEpicShouldGetAllEpicWith200() throws IOException, InterruptedException {
        URI urlPostTask = URI.create("http://localhost:8080/tasks");
        URI urlPostEpic = URI.create("http://localhost:8080/epics");
        HttpRequest request;
        HttpResponse<String> response;

        Task task1 = new Task("Task1", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPostTask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic("Epic2", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPostEpic)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        URI urlGetAll = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder()
                .uri(urlGetAll)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> expectList = manager.getTasksList().stream()
                .filter(task -> task.getClass() == Epic.class)
                .toList();
        List<Task> actualList = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        for (int i = 0; i < actualList.size(); i++) {
            Task actualTask = actualList.get(i);
            Task expectTask = expectList.get(i);
            assertEquals(expectTask.getId(), actualTask.getId());
            assertEquals(expectTask.getName(), actualTask.getName());
            assertEquals(expectTask.getStatus(), actualTask.getStatus());
        }
    }

    @Test
    public void getEpicShouldGetOneEpicWith200() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/epics");
        HttpRequest request;
        HttpResponse<String> response;
        int taskId = 1;

        Epic epic1 = new Epic("Epic1", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic("Epic2", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        URI urlGet = URI.create("http://localhost:8080/epics/" + taskId);
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task expect = manager.getTask(taskId);
        Task actual = gson.fromJson(response.body(), Task.class);

        assertEquals(expect.getId(), actual.getId());
        assertEquals(expect.getName(), actual.getName());
        assertEquals(expect.getStatus(), actual.getStatus());
    }

    @Test
    public void getEpicShouldReturn404() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/epics");
        HttpRequest request;
        HttpResponse<String> response;

        Epic epic1 = new Epic("Epic1", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        URI urlGet = URI.create("http://localhost:8080/tasks/2");
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void getSubtaskShouldReturn200() throws IOException, InterruptedException {
        URI urlPostEpic = URI.create("http://localhost:8080/epics");
        URI urlPostSubtask = URI.create("http://localhost:8080/subtasks");
        HttpRequest request;
        HttpResponse<String> response;
        int taskId = 1;

        Epic epic = new Epic("Epic1", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPostEpic)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask1 = new Subtask("Subtask1", "description", NEW, 1);
        request = HttpRequest.newBuilder()
                .uri(urlPostSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask2 = new Subtask("Subtask2", "description", NEW, 1);
        request = HttpRequest.newBuilder()
                .uri(urlPostSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        URI urlGet = URI.create("http://localhost:8080/epics/" + taskId + "/subtasks");
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> expectList = manager.getTasksList().stream()
                .filter(task -> task.getClass() == Subtask.class)
                .toList();
        List<Task> actualList = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertEquals(expectList.size(), actualList.size());
        for (int i = 0; i < actualList.size(); i++) {
            Task actualTask = actualList.get(i);
            Task expectTask = expectList.get(i);
            assertEquals(expectTask.getId(), actualTask.getId());
            assertEquals(expectTask.getName(), actualTask.getName());
            assertEquals(expectTask.getStatus(), actualTask.getStatus());
        }
    }

    public TaskManager getManager() {
        return manager;
    }

    public HttpTaskServer getTaskServer() {
        return taskServer;
    }

    public HttpClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }
}
