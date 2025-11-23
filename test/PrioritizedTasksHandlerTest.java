import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioritizedTasksHandlerTest extends AbstractHttpTest {

    @Test
    public void testPrioritizedTasks() throws IOException, InterruptedException {
        Task t1 = new Task("A", "d",
                LocalDateTime.now(), Duration.ofMinutes(10));
        Task t2 = new Task("B", "d",
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));

        manager.addTask(t1);
        manager.addTask(t2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);

        assertEquals(200, response.statusCode());

        String body = response.body();

        assertTrue(body.indexOf("A") < body.indexOf("B"));
    }
}