package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);// Добавление задачи в историю

    void remove(int id);      // Новый метод: удаление задачи по id

    List<Task> getHistory();  // Получение истории
}