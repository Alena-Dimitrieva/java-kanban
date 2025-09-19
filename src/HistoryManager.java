import java.util.List;

public interface HistoryManager {
    // Добавление задачи в историю
    void add(Task task);

    // Прочтение истории
    List<Task> getHistory();
}