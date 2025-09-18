import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldCreateEpicWithEmptySubtasks() {
        Epic epic = new Epic("Эпик 1", "Описание эпика");
        assertEquals("Эпик 1", epic.getTitle());
        assertEquals("Описание эпика", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus(), "По умолчанию статус эпика должен быть NEW");
        assertTrue(epic.getSubtaskIds().isEmpty(), "Список подзадач должен быть пустым при создании эпика");
    }

    @Test
    void shouldAddAndRemoveSubtaskIds() {
        Epic epic = new Epic("Эпик", "Описание");
        epic.addSubtaskId(5);
        epic.addSubtaskId(10);

        assertEquals(2, epic.getSubtaskIds().size());
        assertTrue(epic.getSubtaskIds().contains(5));
        assertTrue(epic.getSubtaskIds().contains(10));

        epic.removeSubtaskId(5);
        assertEquals(1, epic.getSubtaskIds().size());
        assertFalse(epic.getSubtaskIds().contains(5));
        assertTrue(epic.getSubtaskIds().contains(10));

        epic.clearSubtasks();
        assertTrue(epic.getSubtaskIds().isEmpty(), "После очистки список подзадач должен быть пустым");
    }

    @Test
    void equalsShouldReturnTrueForSameId() {
        Epic e1 = new Epic("A", "B");
        Epic e2 = new Epic("C", "D");

        e1.setId(1);
        e2.setId(1);

        assertEquals(e1, e2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void equalsShouldReturnFalseForDifferentId() {
        Epic e1 = new Epic("A", "B");
        Epic e2 = new Epic("C", "D");

        e1.setId(1);
        e2.setId(2);

        assertNotEquals(e1, e2, "Эпики с разным id не должны быть равны");
    }

    @Test
    void hashCodeShouldBeSameForSameId() {
        Epic e1 = new Epic("A", "B");
        Epic e2 = new Epic("C", "D");

        e1.setId(1);
        e2.setId(1);

        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void toStringShouldContainAllFields() {
        Epic epic = new Epic("Эпик", "Описание");
        epic.setId(5);
        epic.addSubtaskId(100);
        epic.addSubtaskId(200);
        epic.setStatus(Status.IN_PROGRESS);

        String str = epic.toString();
        assertTrue(str.contains("Эпик"));
        assertTrue(str.contains("Описание"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("IN_PROGRESS"));
        assertTrue(str.contains("100"));
        assertTrue(str.contains("200"));
    }

    @Test
    void epicShouldRemainUnchangedAfterAddingToManager() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        String initialTitle = epic.getTitle();
        String initialDescription = epic.getDescription();
        Status initialStatus = epic.getStatus();

        manager.addEpic(epic);
        Epic saved = manager.getEpicById(epic.getId());

        assertEquals(initialTitle, saved.getTitle());
        assertEquals(initialDescription, saved.getDescription());
        assertEquals(initialStatus, saved.getStatus());
        assertEquals(epic.getId(), saved.getId());
        assertTrue(saved.getSubtaskIds().isEmpty());
    }

    @Test
    void historyShouldContainEpicAfterRetrieval() {
        Epic epic = new Epic("История эпика", "Проверка истории");
        manager.addEpic(epic);

        manager.getEpicById(epic.getId());
        List<Task> history = manager.getHistory();

        assertEquals(1, history.size(), "История должна содержать один эпик");
        assertEquals(epic, history.get(0), "История должна содержать добавленный эпик");
    }
}