import java.util.List;

public interface TaskManager {

    // Методы для Task

    // Возвращает список всех обычных задач
    List<Task> getAllTasks();

    // Удаляет все задачи
    void deleteAllTasks();

    // Возвращает задачу по ID
    Task getTaskById(int id);

    // Добавляет новую задачу
    void addTask(Task task);

    // Обновляет задачу
    void updateTask(Task task);

    // Удаляет задачу по ID
    void deleteTaskById(int id);


    //  Методы для Epic

    // Возвращает список всех эпиков
    List<Epic> getAllEpics();

    // Удаляет все эпики
    void deleteAllEpics();

    // Возвращает эпик по ID
    Epic getEpicById(int id);

    // Добавляет новый эпик
    void addEpic(Epic epic);

    // Обновляет эпик
    void updateEpic(Epic epic);

    // Удаляет эпик по ID
    void deleteEpicById(int id);

    //  Методы для Subtask

    // Возвращает список всех подзадач
    List<Subtask> getAllSubtasks();

    // Удаляет все подзадачи
    void deleteAllSubtasks();

    // Возвращает подзадачу по ID
    Subtask getSubtaskById(int id);

    // Добавляет новую подзадачу
    void addSubtask(Subtask subtask);

    // Обновляет подзадачу
    void updateSubtask(Subtask subtask);

    // Удаляет подзадачу по ID
    void deleteSubtaskById(int id);

    // Возвращает все подзадачи определённого эпика
    List<Subtask> getSubtasksOfEpic(int epicId);


    // История просмотров

    // Возвращает историю просмотров задач
    List<Task> getHistory();


    // Методы приоритета и пересечения времени

    // Возвращает все задачи (Task, Subtask, Epic), отсортированные по времени начала
    List<Task> getPrioritizedTasks();

    // Проверяет, пересекается ли время выполнения новой задачи с уже существующими
    boolean isTimeOverlapping(Task task);
}