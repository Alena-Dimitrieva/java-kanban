import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private final Map<Integer, Node<Task>> nodes = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task == null) return; // игнорирование null

        // Если задача уже есть, удаляем старую
        remove(task.getId());

        Node<Task> node = new Node<>(task);
        linkLast(node);
        nodes.put(task.getId(), node);

        // Ограничиваем историю максимум 10 задач
        while (nodes.size() > 10) {
            remove(head.data.getId());
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        return history;
    }

    private void linkLast(Node<Task> node) {
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    private void remove(int id) {
        Node<Task> node = nodes.remove(id);
        if (node == null) return;

        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;

        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }

        node.prev = null;
        node.next = null;
    }
}