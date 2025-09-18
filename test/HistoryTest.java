import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault(); // создаём новый менеджер перед каждым тестом
    }

    @Test
    void shouldAddTasksToHistory() {
        // создаём разные типы задач
        Task task1 = new Task("Переезд", "Собрать коробки");
        Task task2 = new Task("Учёба", "Сделать дз по Java");
        Epic epic = new Epic("Организовать праздник", "Семейный праздник на 20 человек");
        Subtask subtask = new Subtask("Купить продукты", "Закупиться в магазине", 3);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        // симулируем просмотр задач
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        List<Task> history = manager.getHistory();

        // проверяем порядок и количество
        assertEquals(4, history.size(), "История должна содержать 4 задачи");
        assertEquals(task1, history.get(0), "Первым в истории должен быть task1");
        assertEquals(task2, history.get(1), "Вторым в истории должен быть task2");
        assertEquals(epic, history.get(2), "Третьим в истории должен быть epic");
        assertEquals(subtask, history.get(3), "Четвёртым в истории должен быть subtask");
    }

    @Test
    void shouldNotExceedHistoryLimit() {
        Epic epic = new Epic("Большой эпик", "Много подзадач");
        manager.addEpic(epic);

        // добавляем 11 просмотров одной и той же задачи
        for (int i = 0; i < 11; i++) {
            manager.getEpicById(epic.getId());
        }

        List<Task> history = manager.getHistory();

        // проверяем лимит 10 элементов
        assertEquals(10, history.size(), "История не должна превышать 10 элементов");
    }

    @Test
    void shouldHandleNullTasksGracefully() {
        // проверяем, что null не добавляется в историю
        manager.getTaskById(-1); // несуществующий ID
        List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty(), "История должна оставаться пустой после запроса несуществующей задачи");
    }

    @Test
    void shouldPreserveTaskDataAfterAddingToHistory() {
        Task task = new Task("Тестовая задача", "Описание");
        manager.addTask(task);
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();
        Task saved = history.get(0);

        // проверяем, что данные задачи не изменились при добавлении в историю
        assertEquals(task.getId(), saved.getId(), "ID задачи должен совпадать");
        assertEquals(task.getTitle(), saved.getTitle(), "Название задачи должно совпадать");
        assertEquals(task.getDescription(), saved.getDescription(), "Описание задачи должно совпадать");
        assertEquals(task.getStatus(), saved.getStatus(), "Статус задачи должен совпадать");
    }

    @Test
    void shouldAllowDuplicateTasksInHistory() {
        Task task = new Task("Дубликат", "Описание дубликата");
        manager.addTask(task);

        // дважды просмотрим одну задачу
        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();

        // история может содержать дубликаты
        assertEquals(2, history.size(), "История должна содержать оба просмотра задачи");
        assertEquals(task, history.get(0), "Первый просмотр задачи сохранён");
        assertEquals(task, history.get(1), "Второй просмотр задачи сохранён");
    }
}