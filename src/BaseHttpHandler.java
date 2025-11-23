import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {

    // Универсальный метод отправки ответа
    private void sendResponse(HttpExchange h, int code, String msg) throws IOException {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        h.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(bytes);
        }
    }

    // Отправка объекта в JSON
    protected void sendJson(HttpExchange h, Object obj, int code, Gson gson) throws IOException {
        String json = null;
        try {
            json = gson.toJson(obj);
        } catch (Exception e) {
            throw new RuntimeException (e);
        }
        sendResponse(h, code, json);
    }

    // 200 OK с текстом
    protected void sendText(HttpExchange h, String text) throws IOException {
        sendResponse(h, 200, text);
    }

    // 201
    protected void sendCreated(HttpExchange h, String msg) throws IOException {
        sendResponse(h, 201, msg);
    }

    // 400
    protected void sendBadRequest(HttpExchange h, String msg) throws IOException {
        sendResponse(h, 400, msg);
    }

    // 404
    protected void sendNotFound(HttpExchange h, String msg) throws IOException {
        sendResponse(h, 404, msg);
    }

    // 406 пересечение задач
    protected void sendHasOverlaps(HttpExchange h, String msg) throws IOException {
        sendResponse(h, 406, msg);
    }

    // 500
    protected void sendInternalError(HttpExchange h) throws IOException {
        sendResponse(h, 500, "Внутренняя ошибка сервера");
    }

    // 405
    protected void sendMethodNotAllowed(HttpExchange h) throws IOException {
        sendResponse(h, 405, "Метод не разрешён");
    }
}