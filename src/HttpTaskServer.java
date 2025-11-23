import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

//Главный HTTP сервер приложения для управления задачами.Реализует REST API
public class HttpTaskServer {

    private final HttpServer server;

    //Gson с адаптерами для корректной сериализации LocalDateTime и Duration
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();

    public static final int PORT = 8080;

    //Получение Gson для хэндлеров

    public static Gson getGson() {
        return gson;
    }

    //Конструктор HTTP сервера
    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Регистрация всех контекстов строго по ТЗ
        server.createContext("/task", new TasksHandler(manager, gson));           // задачи
        server.createContext("/epic", new EpicsHandler(manager, gson));           // эпики
        server.createContext("/subtask", new SubtasksHandler(manager, gson));     // подзадачи
        server.createContext("/history", new HistoryHandler(manager, gson));      // история
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson)); // приоритетные задачи
    }

    //Запуск HTTP сервера
    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    //Остановка HTTP сервера
    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    //Точка входа для запуска сервера
    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault(); // фабрика менеджеров по ТЗ
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}