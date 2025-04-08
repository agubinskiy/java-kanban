package history;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node> tasksLinkedMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        int taskId = task.getTaskId();
        if (tasksLinkedMap.containsKey(taskId)) { //Если в истории уже есть эта задача
            remoteNode(tasksLinkedMap.get(taskId));
        }
        linkLast(task);
        if (tail == null)
            tasksLinkedMap.put(taskId, head);
         else
            tasksLinkedMap.put(taskId, tail);
    }

    public void remove(int id) {
        if (tasksLinkedMap.containsKey(id)) {
            Node node = tasksLinkedMap.get(id);
            tasksLinkedMap.remove(id);
            remoteNode(node);
        }
    }

    private void linkLast(Task task) {
        Node node = new Node(task);
        if (head == null) {
            head = node;
        } else if (tail == null) {
            head.setNext(node);
            node.setPrev(head);
            tail = node;
        } else {
            tail.setNext(node);
            node.setPrev(tail);
            tail = node;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        if (node != null) {
            tasks.add(node.getData());
            while (node.getNext() != null) {
                node = node.getNext();
                tasks.add(node.getData());
            }
        }
        return tasks;
    }

    private void remoteNode(Node node) {
        if (node == head) {
            if (tail != null) { //Если есть узлы, кроме головы
                if (node.getNext() != tail) {
                    Node nextNode = node.getNext();
                    nextNode.setPrev(null);
                    head = nextNode;
                } else { //Есть только голова и хвост
                    head = tail;
                    head.setPrev(null);
                    tail = null;
                }
            } else
                head = null;
        } else if (node == tail) {
            if (node.getPrev() != head) { //Если есть узлы, кроме головы и хвоста
                Node prevNode = node.getPrev();
                prevNode.setNext(null);
                tail = prevNode;
            } else {
                tail = null;
                head.setNext(null);
            }
        } else {
            Node prevNode = node.getPrev();
            Node nextNode = node.getNext();
            prevNode.setNext(nextNode);
            nextNode.setPrev(prevNode);
        }
    }
}

 class Node {
    private Task data;
    private Node prev;
    private Node next;

    public Node(Task data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }


    public Task getData() {
        return data;
    }

    public void setData(Task data) {
        this.data = data;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(data, node.data) && Objects.equals(prev, node.prev) && Objects.equals(next, node.next);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, prev, next);
    }

}
