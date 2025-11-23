package handlers;

import com.google.gson.Gson;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class AbstractHttpTest {

    protected HttpTaskServer server;
    protected final Gson gson = HttpTaskServer.getGson();
    protected final HttpClient client = HttpClient.newHttpClient();
    protected TaskManager manager;

    protected final int port = 8080;

    @BeforeEach
    public void startServer() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    public void stopServer() {
        if (server != null) {
            server.stop();

            try {
                Thread.sleep(100); // пауза для закрытия порта
            } catch (InterruptedException ignored) {}
        }
    }

    protected HttpResponse<String> sendRequest(HttpRequest request)
            throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}