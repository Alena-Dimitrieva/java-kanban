import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {

    private File tempFile;
    private FileBackedTaskManager manager;


    @Override
    FileBackedTaskManager getManager() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        return new FileBackedTaskManager(tempFile);
    }

    @BeforeEach
    void setUpFileBackedTaskManager() throws IOException {
        manager = getManager();
    }

    @AfterEach
    void tearDown() {
        tempFile.delete(); // удаление временных файлов после теста
    }

    @Test
    void testSaveLoadEmptyManager() {
        // проверка сохранения и загрузки пустого менеджера

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty(), "Tasks должны быть пустыми");
        assertTrue(loaded.getAllEpics().isEmpty(), "Epics должны быть пустыми");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Subtasks должны быть пустыми");
        assertTrue(loaded.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    void testDeleteTasksAndSave() {
        // Проверка удаления задачи и корректное сохранение состояния в файл
        Task task = new Task("TaskDel", "Desc");
        manager.addTask(task);
        manager.deleteTaskById(task.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty(), "Tasks должны быть пустыми после удаления");
    }
}

