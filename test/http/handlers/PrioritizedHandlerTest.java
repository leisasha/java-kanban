package http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.taskServer.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Task;
import models.adapters.DurationAdapter;
import models.adapters.ZonedDateTimeAdapter;
import models.typeTokens.TaskListTypeToken;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PrioritizedHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = new GsonBuilder()
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
    public void getPrioritizedShouldReturn200() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks");
        HttpRequest request;
        HttpResponse<String> response;

        ZonedDateTime start1 = ZonedDateTime.now();
        ZonedDateTime end1 = start1.plusHours(1);
        ZonedDateTime start2 = ZonedDateTime.now().plusDays(1);
        ZonedDateTime end2 = start2.plusHours(1);

        Task task0 = new Task("Task0", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task0)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task task1 = new Task("Task1", "description", NEW);
        task1.setStartTime(start1);
        task1.setDuration(Duration.between(start1, end1));
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        Task task2 = new Task("Task2", "description", NEW);
        task2.setStartTime(start2);
        task2.setDuration(Duration.between(start2, end2));
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        URI urlGet = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> expectList = manager.getPrioritizedTasks();
        List<Task> actualList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertEquals(2, actualList.size());
        for (int i = 0; i < actualList.size(); i++) {
            Task actualTask = actualList.get(i);
            Task expectTask = expectList.get(i);
            assertEquals(expectTask.getId(), actualTask.getId());
            assertEquals(expectTask.getName(), actualTask.getName());
            assertEquals(expectTask.getStatus(), actualTask.getStatus());
        }
    }
}
