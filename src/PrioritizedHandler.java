import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

// Хэндлер для получения задач в порядке приоритета.
public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            if (!"GET".equals(h.getRequestMethod())) {
                sendMethodNotAllowed(h); // 405
                return;
            }

            sendJson(h, manager.getPrioritizedTasks(), 200, gson); // 200

        } catch (IOException e) {
            sendInternalError(h); // 500
        }
    }
}