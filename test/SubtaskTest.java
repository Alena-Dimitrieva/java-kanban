import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldCreateSubtaskWithEpicId() {
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи", 100);

        assertEquals("Подзадача 1", subtask.getTitle());
        assertEquals("Описание подзадачи", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus(), "По умолчанию статус подзадачи должен быть NEW");
        assertEquals(100, subtask.getEpicId(), "EpicId должен быть установлен при создании подзадачи");
    }

    @Test
    void shouldSetAndGetIdAndStatus() {
        Subtask subtask = new Subtask("T", "Desc", 1);
        subtask.setId(5);
        subtask.setStatus(Status.IN_PROGRESS);

        assertEquals(5, subtask.getId());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
    }

    @Test
    void equalsShouldReturnTrueForSameId() {
        Subtask s1 = new Subtask("A", "B", 1);
        Subtask s2 = new Subtask("C", "D", 2);

        s1.setId(10);
        s2.setId(10);

        assertEquals(s1, s2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void equalsShouldReturnFalseForDifferentId() {
        Subtask s1 = new Subtask("A", "B", 1);
        Subtask s2 = new Subtask("C", "D", 2);

        s1.setId(10);
        s2.setId(20);

        assertNotEquals(s1, s2, "Подзадачи с разным id не должны быть равны");
    }

    @Test
    void hashCodeShouldBeSameForSameId() {
        Subtask s1 = new Subtask("A", "B", 1);
        Subtask s2 = new Subtask("C", "D", 2);

        s1.setId(10);
        s2.setId(10);

        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void toStringShouldContainAllFields() {
        Subtask subtask = new Subtask("Title", "Desc", 99);
        subtask.setId(7);
        subtask.setStatus(Status.DONE);

        String str = subtask.toString();
        assertTrue(str.contains("Title"));
        assertTrue(str.contains("Desc"));
        assertTrue(str.contains("7"));
        assertTrue(str.contains("DONE"));
        assertTrue(str.contains("99"));
    }

    @Test
    void subtaskShouldRemainUnchangedAfterAddingToManager() {
        Subtask subtask = new Subtask("Сделать тест", "Проверка подзадачи", 1);

        int epicId = subtask.getEpicId();
        String initialTitle = subtask.getTitle();
        String initialDescription = subtask.getDescription();
        Status initialStatus = subtask.getStatus();

        manager.addSubtask(subtask);
        Subtask saved = manager.getSubtaskById(subtask.getId());

        assertEquals(initialTitle, saved.getTitle(), "Название подзадачи не должно изменяться");
        assertEquals(initialDescription, saved.getDescription(), "Описание подзадачи не должно изменяться");
        assertEquals(initialStatus, saved.getStatus(), "Статус подзадачи не должен изменяться");
        assertEquals(subtask.getId(), saved.getId(), "Id подзадачи должен оставаться прежним");
        assertEquals(epicId, saved.getEpicId(), "EpicId подзадачи не должен изменяться");
    }

    @Test
    void historyShouldContainSubtaskAfterRetrieval() {
        Subtask subtask = new Subtask("История подзадачи", "Проверка истории", 1);
        manager.addSubtask(subtask);

        manager.getSubtaskById(subtask.getId());
        List<Task> history = manager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну подзадачу");
        assertEquals(subtask, history.get(0), "История должна содержать добавленную подзадачу");
    }
}