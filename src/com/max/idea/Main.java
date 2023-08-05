package com.max.idea;

import java.sql.Time;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {

    private static final String PREFIX_TEMPLATE_FOR_ADDING_AND_TAKING_TASKS = "Task: %d --- ";

    public static void main(String[] args) throws InterruptedException {

        LinkedList<Integer> listBroker = new LinkedList<>();
        Tasks tasks = new Tasks(listBroker);

        Thread addingThread = new Thread(() -> IntStream.iterate(0, i -> i + 1).forEach(i -> {
            if(!Thread.currentThread().isInterrupted()) {
                tasks.addNumber(String.format(PREFIX_TEMPLATE_FOR_ADDING_AND_TAKING_TASKS, i));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }));

        Thread takingThread = new Thread(() -> IntStream.iterate(0, i -> i + 1).forEach(i -> {
            if(!Thread.currentThread().isInterrupted()) {
                tasks.minusNumber(String.format(PREFIX_TEMPLATE_FOR_ADDING_AND_TAKING_TASKS, i));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }));

        Thread stoppingThread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(20);
                System.out.println(Thread.currentThread().getName() + " has stopped all tasks");
                interruptThreads(takingThread, addingThread);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        stoppingThread.setDaemon(true);
        startingThreads(addingThread, takingThread, stoppingThread);
    }

    public static void startingThreads(Thread... threads) {
        Arrays.stream(threads).forEach(Thread::start);
    }
    public static void interruptThreads(Thread... threads) {
        Arrays.stream(threads).forEach(Thread::interrupt);
    }
}
