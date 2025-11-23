import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

//Хэндлер для работы с подзадачами (Subtask)
public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        String method = h.getRequestMethod();

        try {
            switch (method) {
                case "GET" -> handleGet(h);
                case "POST" -> handlePost(h);
                case "DELETE" -> handleDelete(h);
                default -> sendMethodNotAllowed(h); // 405
            }
        } catch (IOException e) {
            sendInternalError(h); // 500
        } catch (IllegalArgumentException e) {
            sendBadRequest(h, "Некорректные данные: " + e.getMessage()); // 400
        } catch (IllegalStateException e) {
            sendHasOverlaps(h, e.getMessage()); // 406 при пересечении задач
        }
    }

    //GET: получить подзадачу по ID или все подзадачи
    private void handleGet(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getIdFromQuery(h);

        if (idOpt.isPresent()) {
            Subtask subtask = manager.getSubtaskById(idOpt.get());
            if (subtask == null) {
                sendNotFound(h, "Subtask not found"); // 404
            } else {
                sendJson(h, subtask, gson); // 200
            }
        } else {
            sendJson(h, manager.getAllSubtasks(), gson); // 200
        }
    }

    //POST: создать новую подзадачу или обновить существующую
    private void handlePost(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (body.isEmpty()) {
            sendBadRequest(h, "Пустое тело запроса"); // 400
            return;
        }

        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask.getId() == 0) {
            manager.addSubtask(subtask);
            sendCreated(h, "Subtask created"); // 201
        } else {
            manager.updateSubtask(subtask);
            sendJson(h, subtask, gson); // 200
        }
    }

    //DELETE: удалить подзадачу по ID или все подзадачи
    private void handleDelete(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getIdFromQuery(h);

        if (idOpt.isPresent()) {
            manager.deleteSubtaskById(idOpt.get());
            sendText(h, "Subtask deleted"); // 200
        } else {
            manager.deleteAllSubtasks();
            sendText(h, "All subtasks deleted"); // 200
        }
    }

    private Optional<Integer> getIdFromQuery(HttpExchange h) {
        String query = h.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            try {
                return Optional.of(Integer.parseInt(query.substring(3)));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Id должен быть числом");
            }
        }
        return Optional.empty();
    }
}