import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicsHandlerTest extends AbstractHttpTest {

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("EpicTest", "Описание эпика");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = sendRequest(request);
        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Epic created"));
        assertEquals(1, manager.getAllEpics().size());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("EpicTest", "Описание");
        manager.addEpic(epic);
        int id = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/epics?id=" + id))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);
        assertEquals(200, response.statusCode());

        Epic returned = gson.fromJson(response.body(), Epic.class);
        assertEquals(id, returned.getId());
        assertEquals("EpicTest", returned.getTitle());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("ToDelete", "desc");
        manager.addEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/epics?id=" + epic.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = sendRequest(request);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic deleted"));

        assertTrue(manager.getAllEpics().isEmpty());
    }
}