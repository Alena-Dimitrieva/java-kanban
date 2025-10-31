import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Метод автосохранения всех задач, эпиков, подзадач и истории
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

            // Сохранение обычных задач
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            // Сохранение эпиков
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            // Сохранение подзадач
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            // Сохраняем историю просмотров как отдельную строку (доп. задание)
            List<Task> history = getHistory();
            if (!history.isEmpty()) {
                writer.write("\n"); // пустая строка перед историей
                StringBuilder historyLine = new StringBuilder();
                for (Task task : history) {
                    historyLine.append(task.getId()).append(",");
                }
                historyLine.deleteCharAt(historyLine.length() - 1); // удалить последнюю запятую
                writer.write(historyLine.toString());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + file.getName(), e);
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        if (task instanceof Epic) {
            sb.append(TaskType.EPIC);
        } else if (task instanceof Subtask) {
            sb.append(TaskType.SUBTASK);
        } else {
            sb.append(TaskType.TASK);
        }
        sb.append(",").append(task.getTitle())
                .append(",").append(task.getStatus())
                .append(",").append(task.getDescription())
                .append(",");

        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        }

        return sb.toString();
    }

    private static Task fromString(String line) {
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    // Метод загрузки менеджера из файла с восстановлением истории
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            if (!file.exists() || file.length() == 0) {
                return manager; // пустой файл
            }

            List<String> lines = Files.readAllLines(file.toPath());

            // Сначала загружаем задачи
            int i = 1; // пропускаем заголовок
            for (; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) break; // пустая строка — конец задач
                Task task = fromString(line);

                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }

                manager.nextId = Math.max(manager.nextId, task.getId() + 1);
            }

            // Если есть строка с историей
            if (i + 1 < lines.size()) {
                String historyLine = lines.get(i + 1).trim();
                if (!historyLine.isEmpty()) {
                    String[] ids = historyLine.split(",");
                    for (String idStr : ids) {
                        int id = Integer.parseInt(idStr);
                        Task task = manager.tasks.get(id);
                        if (task == null) task = manager.epics.get(id);
                        if (task == null) task = manager.subtasks.get(id);
                        if (task != null) {
                            manager.getHistory().add(task); // добавляем в историю
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла: " + file.getName(), e);
        }

        return manager;
    }

    // Переопределяем методы модификации для автосохранения
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    // демонстрация работы менеджера
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // создаём задачи
        Task t1 = new Task("Сходить в спортзал", "Утренняя тренировка");
        manager.addTask(t1);

        Epic epic = new Epic("Подготовить отпуск", "Собрать вещи, купить билеты");
        manager.addEpic(epic);

        Subtask s1 = new Subtask("Купить билеты", "На самолёт в Сочи", epic.getId());
        manager.addSubtask(s1);

        // Просматриваем задачи, чтобы история сформировалась
        manager.getTaskById(t1.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(s1.getId());

        System.out.println("До загрузки:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
        System.out.println("История: " + manager.getHistory());

        // Восстановливление менеджера из файла
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\nПосле загрузки:");
        System.out.println(loaded.getAllTasks());
        System.out.println(loaded.getAllEpics());
        System.out.println(loaded.getAllSubtasks());
        System.out.println("История: " + loaded.getHistory());
    }
}