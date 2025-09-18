import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTaskShouldSaveInHistory() {

        Task task = new Task("Задача 1", "Описание 1");
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        // Проверяем, что задача сохранилась в истории
        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(task, history.get(0), "Задача должна быть сохранена в истории");
    }

    @Test
    void historyShouldNotExceedLimit() {
        // Добавляем 12 задач, а лимит истории = 10
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Задача " + i, "Описание " + i);
            task.setId(i); // задаём ID явно для удобства проверки
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        // История должна содержать только последние 10 задач
        assertEquals(10, history.size(), "История не должна превышать лимит из 10 задач");

        // Проверяем, что самая старая из оставшихся задач — задача с id=3
        assertEquals(3, history.get(0).getId(), "Самая старая задача должна удаляться из истории");
        // Проверяем, что последняя задача — id=12
        assertEquals(12, history.get(history.size() - 1).getId(), "Последняя задача должна быть последней добавленной");
    }

    @Test
    void addNullTaskShouldNotChangeHistory() {
        // Добавляем null, история не должна измениться
        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не должна измениться после добавления null");
    }

    @Test
    void historyShouldReturnNewList() {
        // Проверяем, что возвращается копия списка, а не оригинальный список истории
        Task task = new Task("Задача 1", "Описание 1");
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        history.clear(); // очищаем копию

        // Оригинальная история должна остаться неизменной
        assertEquals(1, historyManager.getHistory().size(),
                "Изменения в возвращённом списке не должны влиять на историю");
    }
}