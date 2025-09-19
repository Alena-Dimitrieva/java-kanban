import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTaskShouldBeStoredInHistory() {
        Task task = new Task("Задача 1", "Описание 1");
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "Добавленная задача должна быть в истории");
    }

    @Test
    void historyShouldNotExceedLimit() {
        // Добавляем 12 задач при лимите = 10
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Задача " + i, "Описание " + i);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "История не должна превышать лимит в 10 задач");
        assertEquals(3, history.get(0).getId(), "Первая задача в истории должна быть с id=3");
        assertEquals(12, history.get(9).getId(), "Последняя задача должна быть с id=12");
    }

    @Test
    void getHistoryShouldReturnCopyOfList() {
        Task task = new Task("Задача 1", "Описание 1");
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        history.clear(); // очищаем копию

        assertEquals(1, historyManager.getHistory().size(),
                "Оригинальная история не должна изменяться при модификации копии");
    }
}