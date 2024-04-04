import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

public class ConcurrentLinkedList {
    private Node head = null;
    private Node tail = null;
    private final Lock lock = new ReentrantLock();

    private class Node {
        int data;
        Node next;
        Node prev;

        Node(int data) {
            this.data = data;
        }
    }

    public void insert(int data) {
        lock.lock();
        try {
            Node newNode = new Node(data);
            insertNode(newNode);
        } finally {
            lock.unlock();
        }
    }

    private void insertNode(Node newNode) {
        if (head == null) {
            head = tail = newNode;
        } else {
            positionAndInsert(newNode);
        }
    }

    private void positionAndInsert(Node newNode) {
        if (newNode.data < head.data) {
            prepend(newNode);
        } else {
            insertSorted(newNode);
        }
    }

    private void prepend(Node newNode) {
        newNode.next = head;
        head.prev = newNode;
        head = newNode;
    }

    private void insertSorted(Node newNode) {
        Node current = head;
        while (current.next != null && current.next.data < newNode.data) {
            current = current.next;
        }
        newNode.next = current.next;
        newNode.prev = current;

        if (current.next != null) {
            current.next.prev = newNode;
        } else {
            tail = newNode;
        }
        current.next = newNode;
    }

    public void remove(int key) {
        lock.lock();
        try {
            if (head != null) {
                removeNode(key);
            }
        } finally {
            lock.unlock();
        }
    }

    private void removeNode(int key) {
        if (head.data == key) {
            head = head.next;
            if (head != null) head.prev = null;
            else tail = null;
        } else {
            removeNonHead(key);
        }
    }

    private void removeNonHead(int key) {
        for (Node current = head; current != null; current = current.next) {
            if (current.data == key) {
                if (current.prev != null) current.prev.next = current.next;
                if (current.next != null) current.next.prev = current.prev;
                else tail = current.prev;
                break;
            }
        }
    }

    public int removeHead() {
        lock.lock();
        try {
            if (head == null) return Integer.MIN_VALUE;
            int value = head.data;
            head = head.next;
            if (head != null) head.prev = null;
            else tail = null;
            return value;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            int count = 0;
            for (Node current = head; current != null; current = current.next) {
                count++;
            }
            return count;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return head == null;
    }

    public boolean contains(int key) {
        lock.lock();
        try {
            for (Node current = head; current != null; current = current.next) {
                if (current.data == key) return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node current = head; current != null; current = current.next) {
            sb.append("[").append(current.data).append("] -> ");
        }
        return sb.append("null").toString();
    }
}
