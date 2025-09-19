import java.util.List;

public interface TaskManager {
    // Методы для Task
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void addTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

    //  Методы для Epic
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void addEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int id);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();
}
