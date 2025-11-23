import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtasksHandlerTest extends AbstractHttpTest {

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask(
                "Sub1", "desc", epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(20)
        );

        String json = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Subtask created"));
        assertEquals(1, manager.getAllSubtasks().size());
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("SubtaskTest", "desc", epic.getId());
        manager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/subtasks?id=" + subtask.getId()))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(200, response.statusCode());
        Subtask returned = gson.fromJson(response.body(), Subtask.class);

        assertEquals(subtask.getId(), returned.getId());
        assertEquals("SubtaskTest", returned.getTitle());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("DeleteMe", "d", epic.getId());
        manager.addSubtask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/subtasks?id=" + subtask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask deleted"));
        assertTrue(manager.getAllSubtasks().isEmpty());
    }
}