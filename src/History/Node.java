package History;

import Tasks.Task;

import java.util.Objects;

public class Node {
    private Task data;
    private Node prev;
    private Node next;

    public Node (Task data) {
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
