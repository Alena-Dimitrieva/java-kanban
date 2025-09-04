public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Задачи
        Task task1 = new Task("Переезд", "Собрать коробки");
        Task task2 = new Task("Учёба", "Сделать дз по Java");
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic = new Epic("Организовать праздник", "Семейный праздник на 20 человек");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Купить продукты", "Закупиться в магазине", epic.getId());
        Subtask sub2 = new Subtask("Заказать торт", "Большой шоколадный торт", epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        // Проверка работы статусов
        System.out.println("Статус эпика сразу: " + manager.getEpicById(epic.getId()).getStatus());

        sub1.setStatus(Status.DONE);
        manager.updateSubtask(sub1);
        System.out.println("После завершения одной подзадачи: " + manager.getEpicById(epic.getId()).getStatus());

        sub2.setStatus(Status.DONE);
        manager.updateSubtask(sub2);
        System.out.println("После завершения всех подзадач: " + manager.getEpicById(epic.getId()).getStatus());

        // Проверка удаления
        manager.deleteSubtaskById(sub1.getId());
        System.out.println("После удаления подзадачи: " + manager.getEpicById(epic.getId()).getSubtaskIds());

        manager.deleteEpicById(epic.getId());
        System.out.println("После удаления эпика: " + manager.getAllEpics());

        manager.deleteAllTasks();
        System.out.println("После удаления всех задач: " + manager.getAllTasks());
    }
}