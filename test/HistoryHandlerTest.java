import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest extends AbstractHttpTest {

    @Test
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void testGetHistoryWithTasks() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Desc");
        manager.addTask(task);
        manager.getTaskById(task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task1"));
    }
}