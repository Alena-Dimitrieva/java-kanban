package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

//Хэндлер для работы с эпиками (Epic)
public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
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
        }
    }

    //GET: получить эпик по ID или все эпики
    private void handleGet(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getIdFromQuery(h);

        if (idOpt.isPresent()) {
            Epic epic = manager.getEpicById(idOpt.get());
            if (epic == null) {
                sendNotFound(h, "Epic not found"); // 404
            } else {
                sendJson(h, epic, gson); // 200
            }
        } else {
            sendJson(h, manager.getAllEpics(), gson); // 200
        }
    }

    //POST: создать новый эпик или обновить существующий
    private void handlePost(HttpExchange h) throws IOException {
        String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (body.isEmpty()) {
            sendBadRequest(h, "Пустое тело запроса"); // 400
            return;
        }

        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() == 0) {
            manager.addEpic(epic);
            sendCreated(h, "Epic created"); // 201
        } else {
            manager.updateEpic(epic);
            sendJson(h, epic, gson); // 200
        }
    }

    //DELETE: удалить эпик по ID или все эпики
    private void handleDelete(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getIdFromQuery(h);

        if (idOpt.isPresent()) {
            manager.deleteEpicById(idOpt.get());
            sendText(h, "Epic deleted"); // 200
        } else {
            manager.deleteAllEpics();
            sendText(h, "All epics deleted"); // 200
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