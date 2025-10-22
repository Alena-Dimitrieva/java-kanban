import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// HistoryTest — интеграционные тесты истории через TaskManager
class HistoryTest {

    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
    }

    // Проверка, что задачи добавляются в историю и сохраняется порядок
    @Test
    void shouldAddTasksToHistory() {
        Task task1 = new Task("Переезд", "Собрать коробки");
        Task task2 = new Task("Учёба", "Сделать дз по Java");
        Epic epic = new Epic("Организовать праздник", "Семейский праздник на 20 человек");
        Subtask subtask = new Subtask("Купить продукты", "Закупиться в магазине", 3);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        // симулятор просмотра задач
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        List<Task> history = manager.getHistory();

        assertEquals(4, history.size(), "История должна содержать 4 задачи");
        assertEquals(task1, history.get(0), "Первым в истории должен быть task1");
        assertEquals(task2, history.get(1), "Вторым в истории должен быть task2");
        assertEquals(epic, history.get(2), "Третьим в истории должен быть epic");
        assertEquals(subtask, history.get(3), "Четвёртым в истории должен быть subtask");
    }

    @Test
    void shouldMaintainCorrectOrderWithoutLimit() {
        // Создаём 12 задач
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Task" + i, "Desc" + i);
            manager.addTask(task);
            manager.getTaskById(task.getId());
        }

        List<Task> history = manager.getHistory();

        // Проверяем, что все 12 задач присутствуют
        assertEquals(12, history.size(), "История должна содержать все 12 просмотров");

        // Проверяем порядок просмотров
        for (int i = 0; i < 12; i++) {
            assertEquals("Task" + (i + 1), history.get(i).getTitle(),
                    "Задачи должны быть в порядке последних просмотров");
        }

        // Проверка повторного просмотра: Task5 ещё раз
        manager.getTaskById(5);
        history = manager.getHistory();

        // Теперь Task5 должен быть последним
        assertEquals("Task5", history.get(history.size() - 1).getTitle(),
                "Повторный просмотр задачи должен перемещать её в конец истории");
    }

    // Проверка, что null задачи не добавляются
    @Test
    void shouldHandleNullTasksGracefully() {
        manager.getTaskById(-1); // несуществующий ID

        List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty(), "История должна оставаться пустой после запроса несуществующей задачи");
    }

    // Проверка, что данные задачи не изменяются при добавлении в историю
    @Test
    void shouldPreserveTaskDataAfterAddingToHistory() {
        Task task = new Task("Тестовая задача", "Описание");
        manager.addTask(task);
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();
        Task saved = history.get(0);

        assertEquals(task.getId(), saved.getId(), "ID задачи должен совпадать");
        assertEquals(task.getTitle(), saved.getTitle(), "Название задачи должно совпадать");
        assertEquals(task.getDescription(), saved.getDescription(), "Описание задачи должно совпадать");
        assertEquals(task.getStatus(), saved.getStatus(), "Статус задачи должен совпадать");
    }

    // Новый вариант теста: проверка повторного просмотра перемещает задачу в конец истории
    @Test
    void shouldMoveTaskToEndWhenViewedAgain() {
        Task task = new Task("Дубликат", "Описание дубликата");
        manager.addTask(task);

        manager.getTaskById(task.getId()); // первый просмотр
        manager.getTaskById(task.getId()); // второй просмотр

        List<Task> history = manager.getHistory();

        // Дубликатов нет, задача просто перемещается в конец истории
        assertEquals(1, history.size(), "Повторный просмотр не должен создавать дубликат");
        assertEquals(task, history.get(0), "Задача должна оставаться в истории и быть последней просмотренной");
    }

    // Новый тест: проверка, что история сохраняет порядок просмотров разных типов задач
    @Test
    void shouldMaintainCorrectOrderWithMixedTypes() {
        Task task = new Task("Task", "Desc");
        Epic epic = new Epic("Epic", "Desc");
        Subtask subtask = new Subtask("Subtask", "Desc", 2);

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        manager.getEpicById(epic.getId());
        manager.getTaskById(task.getId());
        manager.getSubtaskById(subtask.getId());
        manager.getEpicById(epic.getId()); // повторный просмотр epic

        List<Task> history = manager.getHistory();

        assertEquals(3, history.size(), "История должна содержать только уникальные просмотры");
        assertEquals(task, history.get(0));
        assertEquals(subtask, history.get(1));
        assertEquals(epic, history.get(2), "Повторный просмотр epic переместил его в конец");
    }
}