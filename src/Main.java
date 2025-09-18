public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault(); //фабрика менеджеров

        // Создаём задачи
        Task task1 = new Task("Переезд", "Собрать коробки");
        Task task2 = new Task("Учёба", "Сделать дз по Java");
        manager.addTask(task1);
        manager.addTask(task2);

        // Эпик + подзадачи
        Epic epic = new Epic("Организовать праздник", "Семейный праздник на 20 человек");
        manager.addEpic(epic);

        Subtask sub1 = new Subtask("Купить продукты", "Закупиться в магазине", epic.getId());
        Subtask sub2 = new Subtask("Заказать торт", "Большой шоколадный торт", epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);

        // Получаем несколько задач (чтобы история заполнилась)
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(sub1.getId());
        manager.getSubtaskById(sub2.getId());

        // Печать всех задач и истории
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}