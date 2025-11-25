package handlers;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TasksHandlerTest extends AbstractHttpTest {

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(
                "TaskTest", "Описание",
                LocalDateTime.now(), Duration.ofMinutes(30)
        );

        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Task created"));
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("ReadMe", "desc");
        manager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/tasks?id=" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(200, response.statusCode());

        Task returned = gson.fromJson(response.body(), Task.class);

        assertEquals(task.getId(), returned.getId());
        assertEquals("ReadMe", returned.getTitle());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("DeleteMe", "d");
        manager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/tasks?id=" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task deleted"));

        assertTrue(manager.getAllTasks().isEmpty());
    }
}