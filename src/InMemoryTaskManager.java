import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected final HistoryManager historyManager;

    // Хранение задач по приоритету (по startTime)
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(
            Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())
    ));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public InMemoryTaskManager() {
        this(Managers.getDefaultHistory());
    }

    protected int generateId() {
        return nextId++;
    }

    // Методы для Task

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void addTask(Task task) {
        if (isTimeOverlapping(task)) {
            System.out.println("Невозможно добавить задачу: время пересекается с другой задачей");
            return;
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            if (isTimeOverlapping(task)) {
                System.out.println("Невозможно обновить задачу: время пересекается с другой задачей");
                prioritizedTasks.add(tasks.get(task.getId()));
                return;
            }
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
        }
    }

    // Методы для Epic

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            for (int subId : epic.getSubtaskIds()) {
                historyManager.remove(subId);
                prioritizedTasks.remove(subtasks.get(subId));
            }
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                prioritizedTasks.remove(subtasks.get(subId));
                subtasks.remove(subId);
                historyManager.remove(subId);
            }
            historyManager.remove(id);
        }
    }

    // Методы для Subtask

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();

        // После удаления всех подзадач эпики очищаются и обновляются
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.updateTimeAndDuration(getSubtasksOfEpic(epic.getId())); // пересчёт времени
            updateEpicStatus(epic);
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isTimeOverlapping(subtask)) {
            System.out.println("Невозможно добавить подзадачу: время пересекается с другой задачей");
            return;
        }

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());

            //При добавлении пересчет времени и длительность эпика
            epic.updateTimeAndDuration(getSubtasksOfEpic(epic.getId()));

            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));

            if (isTimeOverlapping(subtask)) {
                System.out.println("Пересечение по времени — обновление отменено");
                prioritizedTasks.add(subtasks.get(subtask.getId()));
                return;
            }

            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateTimeAndDuration(getSubtasksOfEpic(epic.getId()));

                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove((Integer) id);

                epic.updateTimeAndDuration(getSubtasksOfEpic(epic.getId()));

                updateEpicStatus(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }
        List<Subtask> result = new ArrayList<>();
        for (int subId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    // Логика пересчёта статуса и проверки пересечений

    private void updateEpicStatus(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksOfEpic(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // История просмотров

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Приоритет и проверки

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isTimeOverlapping(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        for (Task existing : prioritizedTasks) {
            if (existing.getStartTime() == null || existing.getEndTime() == null) {
                continue;
            }

            boolean overlap = !task.getEndTime().isBefore(existing.getStartTime())
                    && !task.getStartTime().isAfter(existing.getEndTime());

            if (overlap && existing.getId() != task.getId()) {
                return true;
            }
        }
        return false;
    }
}