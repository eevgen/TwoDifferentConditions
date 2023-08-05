package com.max.idea;

import java.util.LinkedList;
import java.util.Random;
import java.util.RandomAccess;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Tasks {

    private LinkedList<Integer> linkedList;
    private final Lock lock = new ReentrantLock();
    private final Condition addCondition = lock.newCondition();
    private final Condition takingCondition = lock.newCondition();

    public Tasks(LinkedList<Integer> linkedList) {
        this.linkedList = linkedList;
    }

    public void addNumber(String prefix) {
        lock.lock();
        while (linkedList.size() >= 90) {
            try {
                addCondition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        int i = (new Random()).nextInt(1, 100);
        linkedList.add(i);
        System.out.printf(prefix + "Success in adding number %d with thread %s\n", i, Thread.currentThread().getName());
        takingCondition.signal();
        lock.unlock();
    }

    public void minusNumber(String prefix) {
        lock.lock();
        if(!linkedList.isEmpty()) {
            while (linkedList.size() <= 10) {
                try {
                    takingCondition.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            int i = linkedList.getLast();
            linkedList.removeLast();
            System.out.printf(prefix + "Success in removing number %d with thread %s\n", i, Thread.currentThread().getName());
            addCondition.signal();
        }
        lock.unlock();
    }

}
