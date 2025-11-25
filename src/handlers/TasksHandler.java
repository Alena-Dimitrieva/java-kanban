package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

// Хэндлер для работы с обычными задачами (Task)
public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
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
            sendHasOverlaps(h, e.getMessage()); // 406
        }
    }

    //GET: получить одну задачу по ID или все задачи
    private void handleGet(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getIdFromQuery(h);

        if (idOpt.isPresent()) {
            Task task = manager.getTaskById(idOpt.get());
            if (task == null) {
                sendNotFound(h, "Task not found"); // 404
            } else {
                sendJson(h, task, gson); // 200
            }
        } else {
            sendJson(h, manager.getAllTasks(), gson); // 200
        }
    }

    //POST: создать новую задачу или обновить существующую
    private void handlePost(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (body.isEmpty()) {
            sendBadRequest(h, "Пустое тело запроса"); // 400
            return;
        }

        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            manager.addTask(task);
            sendCreated(h, "Task created"); // 201
        } else {
            manager.updateTask(task);
            sendJson(h, task, gson); // 200
        }
    }

    //DELETE: удалить задачу по ID или все задачи
    private void handleDelete(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getIdFromQuery(h);

        if (idOpt.isPresent()) {
            manager.deleteTaskById(idOpt.get());
            sendText(h, "Task deleted"); // 200 OK
        } else {
            manager.deleteAllTasks();
            sendText(h, "All tasks deleted"); // 200 OK
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