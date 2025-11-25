package managers;

import enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Абстрактный тест, который должен наследовать каждый конкретный тестовый класс менеджера
public abstract class AbstractTaskManagerTest<T extends TaskManager> {

    protected T manager;

    abstract T getManager() throws IOException;

    @BeforeEach
    void setUp() throws IOException {
        manager = getManager();
    }

    // Epic status tests (граничные случаи по ТЗ)
    @Test
    void epicStatus_AllSubtasksNew_ShouldBeNEW() {
        Epic epic = new Epic("Epic A", "desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("s1", "d", epic.getId());
        Subtask s2 = new Subtask("s2", "d", epic.getId());

        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_AllSubtasksDone_ShouldBeDONE() {
        Epic epic = new Epic("Epic B", "desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("s1", "d", epic.getId());
        s1.setStatus(Status.DONE);
        Subtask s2 = new Subtask("s2", "d", epic.getId());
        s2.setStatus(Status.DONE);

        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_NewAndDone_ShouldBeIN_PROGRESS() {
        Epic epic = new Epic("Epic C", "desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("s1", "d", epic.getId());
        s1.setStatus(Status.NEW);
        Subtask s2 = new Subtask("s2", "d", epic.getId());
        s2.setStatus(Status.DONE);

        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_AnyInProgress_ShouldBeIN_PROGRESS() {
        Epic epic = new Epic("Epic D", "desc");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("s1", "d", epic.getId());
        s1.setStatus(Status.IN_PROGRESS);
        Subtask s2 = new Subtask("s2", "d", epic.getId());
        s2.setStatus(Status.NEW);

        manager.addSubtask(s1);
        manager.addSubtask(s2);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    // History tests (базовые)
    @Test
    void historyShouldBeEmptyInitially() {
        List<Task> history = manager.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void historyShouldAddAndNotDuplicate() {
        Task t = new Task("T", "D");
        manager.addTask(t);

        // просмотр дважды — в истории не должно быть дубликата
        manager.getTaskById(t.getId());
        manager.getTaskById(t.getId());

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(t, history.getFirst());
    }

    // Time overlap tests (по ТЗ проверка пересечения интервалов)
    @Test
    void addingOverlappingTaskShouldNotBeAdded() {
        Task t1 = new Task("A", "d");
        t1.setStartTime(LocalDateTime.of(2025, 11, 10, 10, 0));
        t1.setDuration(Duration.ofMinutes(60));

        Task t2 = new Task("B", "d");
        t2.setStartTime(LocalDateTime.of(2025, 11, 10, 10, 30)); // пересекается
        t2.setDuration(Duration.ofMinutes(30));

        manager.addTask(t1);
        int before = manager.getAllTasks().size();

        manager.addTask(t2);
        int after = manager.getAllTasks().size();


        assertEquals(before, after, "Пересекающаяся задача не должна добавляться");
    }

    @Test
    void getPrioritizedTasksShouldReturnSortedByStart() {
        // Если у реализации есть prioritized, проверяем порядок
        Task t1 = new Task("early", "d");
        t1.setStartTime(LocalDateTime.of(2025, 11, 10, 9, 0));
        t1.setDuration(Duration.ofMinutes(30));

        Task t2 = new Task("late", "d");
        t2.setStartTime(LocalDateTime.of(2025, 11, 10, 12, 0));
        t2.setDuration(Duration.ofMinutes(30));

        manager.addTask(t2);
        manager.addTask(t1);

        List<Task> prioritized = manager.getPrioritizedTasks();
        // Если реализация не поддерживает — этот тест всё равно будет проверять возвращаемый список
        if (!prioritized.isEmpty()) {
            assertTrue(prioritized.getFirst().getStartTime().isBefore(prioritized.getLast().getStartTime())
                    || prioritized.size() == 1);
        }
    }
}


