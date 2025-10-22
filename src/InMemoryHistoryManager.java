import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap = new HashMap<>(); // для быстрого доступа по id
    private Node head; // начало списка
    private Node tail; // конец списка

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        // Если задача уже есть — удаляется старый узел
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        // Добавление задачи в конец списка
        linkLast(task);
    }

    // Метод добавления узла в конец списка
    private void linkLast(Task task) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        nodeMap.put(task.getId(), newNode); // сохранение в HashMap
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    // Удаление узла из двусвязного списка
    private void removeNode(Node node) {
        nodeMap.remove(node.task.getId()); // теперь удаление здесь

        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    // Вложенный класс перенесён в конец
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }
}