package manager;

import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager{
    private Node<Task> head;
    private Node<Task> tail;
    private final HashMap<Integer, Node<Task>> historyTaskList = new HashMap<>();

    public void add(Task task) {
        if (historyTaskList.containsKey(task.getId())) {
            removeNode(historyTaskList.remove(task.getId()));
        }
        linkLast(task);
    }
    public void remove(int id) {
        if (historyTaskList.containsKey(id))
            removeNode(historyTaskList.remove(id));
    }
    public List<Task> getHistory() {
        List<Task> resultFunction = new ArrayList<>();

        if (head != null) {
            Node<Task> currentNode = head;
            while(currentNode != null) {
                resultFunction.add(currentNode.getData());
                currentNode = currentNode.getNextNode();
            }
        }

        return resultFunction;
    }

    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);

        if (tail != null && head != null) {
            newNode.setLastNode(tail);
            tail.setNextNode(newNode);
            tail = newNode;
        } else {
            head = newNode;
            tail = newNode;
        }

        historyTaskList.put(task.getId(), newNode);
    }
    private void removeNode(Node<Task> node) {
        Node<Task> lastNode = node.getLastNode();
        Node<Task> nextNode = node.getNextNode();

        if (lastNode != null) {
            lastNode.setNextNode(nextNode);
        } else {
            head = nextNode;
        }

        if (nextNode != null) {
            nextNode.setLastNode(lastNode);
        } else {
            tail = lastNode;
        }
    }
}

class Node<T> {
    private Node<T> nextNode;
    private Node<T> lastNode;
    private T data;

    public Node(){}

    public Node(T data) {
        this.data = data;
        this.nextNode = null;
        this.lastNode = null;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }

    public Node<T> getLastNode() {
        return lastNode;
    }

    public void setLastNode(Node<T> lastNode) {
        this.lastNode = lastNode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
