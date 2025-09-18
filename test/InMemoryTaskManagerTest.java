import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    //  Task тест
    @Test
    void shouldAddAndGetTask() {
        Task task = new Task("Задача 1", "Описание задачи");
        manager.addTask(task);

        Task saved = manager.getTaskById(task.getId());

        assertNotNull(saved);
        assertEquals(task, saved);
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Задача", "Описание");
        manager.addTask(task);

        task.setStatus(Status.DONE);
        manager.updateTask(task);

        Task updated = manager.getTaskById(task.getId());
        assertEquals(Status.DONE, updated.getStatus());
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task("Задача", "Описание");
        manager.addTask(task);

        manager.deleteTaskById(task.getId());
        assertNull(manager.getTaskById(task.getId()));
    }

    @Test
    void getAllTasksShouldReturnAllTasks() {
        Task task1 = new Task("T1", "Desc1");
        Task task2 = new Task("T2", "Desc2");
        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1) && tasks.contains(task2));
    }

    // Epic и Subtask тесты
    @Test
    void shouldAddEpicAndSubtasks() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Подзадача 1", "Описание", epic.getId());
        Subtask sub2 = new Subtask("Подзадача 2", "Описание", epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        List<Subtask> subtasks = manager.getSubtasksOfEpic(epic.getId());
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(sub1) && subtasks.contains(sub2));
    }

    @Test
    void shouldUpdateEpicStatus() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Под1", "Desc", epic.getId());
        Subtask sub2 = new Subtask("Под2", "Desc", epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.NEW);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void shouldDeleteEpicWithSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);

        Subtask sub = new Subtask("Подзадача", "Описание", epic.getId());
        manager.addSubtask(sub);

        manager.deleteEpicById(epic.getId());

        assertNull(manager.getEpicById(epic.getId()));
        assertNull(manager.getSubtaskById(sub.getId()));
    }

    @Test
    void getAllEpicsAndSubtasksShouldReturnAll() {
        Epic epic = new Epic("Эпик", "Описание");
        manager.addEpic(epic);
        Subtask sub = new Subtask("Под", "Desc", epic.getId());
        manager.addSubtask(sub);

        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    //  History тест
    @Test
    void shouldAddTasksToHistory() {
        Task task = new Task("Задача", "Описание");
        manager.addTask(task);
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void historyShouldNotExceedLimit() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.addEpic(epic);

        // добавляем более 10 просмотров одного эпика
        for (int i = 0; i < 12; i++) {
            manager.getEpicById(epic.getId());
        }

        List<Task> history = manager.getHistory();
        assertTrue(history.size() <= 10, "История не должна превышать 10 элементов");
    }

    @Test
    void addingMultipleTasksAndEpicsShouldWorkCorrectly() {
        Task task = new Task("T", "Desc");
        Epic epic = new Epic("E", "Desc");
        manager.addTask(task);
        manager.addEpic(epic);

        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
    }
}