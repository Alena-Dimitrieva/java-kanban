import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldCreateTaskWithTitleAndDescription() {
        Task task = new Task("Задача 1", "Описание 1");

        assertEquals("Задача 1", task.getTitle());
        assertEquals("Описание 1", task.getDescription());
        assertEquals(Status.NEW, task.getStatus(), "По умолчанию статус задачи должен быть NEW");
    }

    @Test
    void shouldSetAndGetId() {
        Task task = new Task("T", "Desc");
        task.setId(42);
        assertEquals(42, task.getId());
    }

    @Test
    void shouldSetAndGetStatus() {
        Task task = new Task("T", "Desc");
        task.setStatus(Status.DONE);
        assertEquals(Status.DONE, task.getStatus());
    }

    @Test
    void equalsShouldReturnTrueForSameId() {
        Task task1 = new Task("T1", "Desc1");
        Task task2 = new Task("T2", "Desc2");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void equalsShouldReturnFalseForDifferentId() {
        Task task1 = new Task("T1", "Desc1");
        Task task2 = new Task("T2", "Desc2");

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2, "Задачи с разным id не должны быть равны");
    }

    @Test
    void hashCodeShouldBeSameForSameId() {
        Task task1 = new Task("T1", "Desc1");
        Task task2 = new Task("T2", "Desc2");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void toStringShouldContainAllFields() {
        Task task = new Task("Title", "Description");
        task.setId(5);
        task.setStatus(Status.IN_PROGRESS);

        String str = task.toString();
        assertTrue(str.contains("Title"));
        assertTrue(str.contains("Description"));
        assertTrue(str.contains("5"));
        assertTrue(str.contains("IN_PROGRESS"));
    }

    @Test
    void taskShouldRemainUnchangedAfterAddingToManager() {
        Task task = new Task("Сходить в магазин", "Купить продукты");
        Status initialStatus = task.getStatus();
        String initialTitle = task.getTitle();
        String initialDescription = task.getDescription();

        manager.addTask(task);
        Task savedTask = manager.getTaskById(task.getId());

        assertEquals(initialTitle, savedTask.getTitle(), "Название задачи не должно изменяться");
        assertEquals(initialDescription, savedTask.getDescription(), "Описание задачи не должно изменяться");
        assertEquals(initialStatus, savedTask.getStatus(), "Статус задачи не должен изменяться");
        assertEquals(task.getId(), savedTask.getId(), "Id задачи должен оставаться прежним");
    }

    @Test
    void historyShouldContainTaskAfterRetrieval() {
        Task task = new Task("История", "Проверка истории");
        manager.addTask(task);

        manager.getTaskById(task.getId());
        List<Task> history = manager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "История должна содержать добавленную задачу");
    }
}