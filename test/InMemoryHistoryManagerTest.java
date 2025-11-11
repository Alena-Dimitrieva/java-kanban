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
        assertEquals(task, history.getFirst(), "Добавленная задача должна быть в истории");
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

    // Новые тесты по ТЗ — удаление из истории: начало, середина, конец
    @Test
    void removeFromHistory_beginning_middle_end() {
        // создание трех задач и добавление в историю в порядке 1,2,3
        Task t1 = new Task("T1", "D1");
        Task t2 = new Task("T2", "D2");
        Task t3 = new Task("T3", "D3");

        // Прописала уникальные id — иначе все будут id == 0 и будут конфликтовать в nodeMap
        t1.setId(1);
        t2.setId(2);
        t3.setId(3);

        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);

        // Проверка, что добавились все три
        List<Task> hist = historyManager.getHistory();
        assertEquals(3, hist.size(), "Ожидаем 3 элемента в истории перед удалениями");

        // 1) удалить начало (t1)
        historyManager.remove(t1.getId());
        hist = historyManager.getHistory();
        assertEquals(2, hist.size(), "После удаления начала должно остаться 2 элемента");
        assertFalse(hist.contains(t1), "t1 не должен присутствовать в истории после удаления");

        // 2) удалить середину (t2)
        historyManager.remove(t2.getId());
        hist = historyManager.getHistory();
        assertEquals(1, hist.size(), "После удаления середины должен остаться 1 элемент");
        assertFalse(hist.contains(t2), "t2 не должен присутствовать в истории после удаления");

        // 3) удалить конец (t3)
        historyManager.remove(t3.getId());
        hist = historyManager.getHistory();
        assertEquals(0, hist.size(), "После удаления всех элементов история должна быть пустой");
    }

    @Test
    void shouldNotAddNullToHistory() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty(), "Null не должен добавляться в историю");
    }

    @Test
    void addingDuplicateShouldMoveToEndNotDuplicateEntries() {
        Task t = new Task("Dup", "D");
        historyManager.add(t);
        historyManager.add(t);

        List<Task> hist = historyManager.getHistory();
        assertEquals(1, hist.size(), "Повторный add не должен создавать дубликат в истории");
        assertEquals(t, hist.getFirst());
    }
}