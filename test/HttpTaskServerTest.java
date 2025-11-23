import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest extends AbstractHttpTest {

    @Test
    public void testServerStartsAndResponds() throws IOException, InterruptedException {
        //  GET на /tasks
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);

        // Если сервер поднялся — он не вернёт ошибку соединения
        assertNotNull(response);
        assertEquals(200, response.statusCode());
    }
}