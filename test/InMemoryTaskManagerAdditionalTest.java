import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerAdditionalTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void updateTaskUpdateSubtaskUpdateEpicShouldSaveChanges() {
        // Создаём задачи
        Task task = new Task("Task1", "Description1");
        manager.addTask(task);

        Epic epic = new Epic("Epic1", "Epic Description");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask Description", epic.getId());
        manager.addSubtask(subtask);

        // Обновляем
        task = new Task("Updated Task", "Updated Description");
        task.setId(manager.getAllTasks().get(0).getId());
        manager.updateTask(task);

        epic = new Epic("Updated Epic", "Updated Epic Description");
        epic.setId(manager.getAllEpics().get(0).getId());
        manager.updateEpic(epic);

        subtask = new Subtask("Updated Subtask", "Updated Subtask Description", epic.getId());
        subtask.setId(manager.getAllSubtasks().get(0).getId());
        manager.updateSubtask(subtask);

        // Проверяем, что обновления сохранились
        Task updatedTask = manager.getTaskById(task.getId());
        assertEquals("Updated Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals("Updated Epic", updatedEpic.getTitle());
        assertEquals("Updated Epic Description", updatedEpic.getDescription());

        Subtask updatedSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals("Updated Subtask", updatedSubtask.getTitle());
        assertEquals("Updated Subtask Description", updatedSubtask.getDescription());
    }

    @Test
    void deleteEpicShouldAlsoDeleteSubtasks() {
        Epic epic = new Epic("Epic for Deletion", "Epic Description");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Sub1 Desc", epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Sub2 Desc", epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        // Убедимся, что подзадачи существуют
        assertEquals(2, manager.getSubtasksOfEpic(epic.getId()).size());

        // Удаляем эпик
        manager.deleteEpicById(epic.getId());

        // Проверяем, что подзадачи удалены
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void historyShouldTrackMixedGetCalls() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task", "Task Desc");
        Epic epic = new Epic("Epic", "Epic Desc");
        Subtask subtask = new Subtask("Subtask", "Sub Desc", 2);

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        // Смешанные вызовы get
        manager.getTaskById(task.getId());      // Task просмотрен
        manager.getEpicById(epic.getId());      // Epic просмотрен
        manager.getSubtaskById(subtask.getId()); // Subtask просмотрен
        manager.getEpicById(epic.getId());      // Epic просмотрен снова

        List<Task> history = manager.getHistory();

        // Проверяем размер истории
        assertEquals(3, history.size(), "История должна содержать три уникальных элемента");

        // Проверяем, что все элементы присутствуют
        assertTrue(history.contains(task), "История должна содержать Task");
        assertTrue(history.contains(epic), "История должна содержать Epic");
        assertTrue(history.contains(subtask), "История должна содержать Subtask");

        // Проверяем порядок последних просмотров (последний просмотр перемещает элемент в конец)
        assertEquals(task, history.get(0), "Task должен быть первым просмотренным");
        assertEquals(subtask, history.get(1), "Subtask должен быть вторым просмотренным");
        assertEquals(epic, history.get(2), "Epic должен быть последним после повторного просмотра");
    }

    @Test
    void historyShouldNotExceedLimit() {
        // Создаём 12 задач
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Task" + i, "Desc" + i);
            manager.addTask(task);
            manager.getTaskById(task.getId());
        }

        List<Task> history = manager.getHistory();

        // Проверяем, что в истории только 10 последних
        assertEquals(10, history.size(), "История не должна превышать лимит из 10 задач");

        // Проверяем, что это последние 10 задач
        for (int i = 0; i < 10; i++) {
            assertEquals("Task" + (i + 3), history.get(i).getTitle());
        }
    }
}