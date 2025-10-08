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
    void getHistoryShouldReturnCopyOfList() {
        Task task = new Task("Задача 1", "Описание 1");
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        history.clear(); // очищаем копию

        assertEquals(1, historyManager.getHistory().size(),
                "Оригинальная история не должна изменяться при модификации копии");
    }
}