package com.superkele.translation.test.completablefuture;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AsyncDemo {

    Map<Task, Short> indexTaskMap = new HashMap<>();

    AtomicInteger event = new AtomicInteger();

    List<Short> eventMask = new ArrayList<>();

    Map<Short, List<TaskWrapper>> eventMap = new HashMap<>();

    PriorityQueue<TaskWrapper> queue = new PriorityQueue<>(Comparator.comparing(TaskWrapper::getIndex));

    List<CompletableFuture> futures = new ArrayList<>();

    @Test
    public void test() {
        /**
         * 事件掩码，可触发的事件
         */
        System.out.println(Thread.currentThread());
        Task task1 = new Task("任务1");
        Task task2 = new Task("任务2");
        task2.sort = 1;
        task2.async = true;
        Task task3 = new Task("任务3");
        task3.sort = 1;
        task3.async = true;
        Task task4 = new Task("任务4");
        task4.sort = 2;
        task4.after = new Task[]{task2, task3};
        List<Task> list = List.of(task1, task2, task3, task4);
        List<Task> sortedList = list.stream()
                .sorted(Comparator.comparingInt(Task::getSort))
                .collect(Collectors.toList());
        short index = 1;
        List<TaskWrapper> taskWrappers = new ArrayList<>();
        for (int i = 0; i < sortedList.size(); i++) {
            Task task = sortedList.get(i);
            indexTaskMap.put(task, index);
            TaskWrapper taskWrapper = new TaskWrapper(task, index, getConsumer());
            taskWrappers.add(taskWrapper);
            index <<= 1;
        }
        taskWrappers.forEach(taskWrapper -> {
            Task task = taskWrapper.getTask();
            if (task.after != null) {
                short mask = 0;
                for (Task after : task.after) {
                    mask |= indexTaskMap.get(after);
                }
                List<TaskWrapper> tasks = eventMap.computeIfAbsent(mask, k -> new ArrayList<>());
                tasks.add(taskWrapper);
                eventMask.add(mask);
            }else{
                queue.add(taskWrapper);
            }
        });
        while (!queue.isEmpty()) {
            TaskWrapper taskWrapper = queue.poll();
            taskWrapper.getConsumer().accept(taskWrapper.task);
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    public Consumer<Task> getConsumer() {
        return task -> {
            if (!task.async) {
                consume(task);
            } else {
                CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                    consume(task);
                });
                futures.add(voidCompletableFuture);
            }
        };
    }

    public void consume(Task task) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread() + ":" + task.getName() + "被消费了");
        event.updateAndGet(v -> v | indexTaskMap.get(task));
        for (Short i : eventMask) {
            if ((event.get() & i) == i) {
                List<TaskWrapper> taskWrappers = eventMap.get(i);
                Iterator<TaskWrapper> iterator = taskWrappers.iterator();
                while (iterator.hasNext()) {
                    TaskWrapper taskWrapper = iterator.next();
                    iterator.remove();
                    taskWrapper.getConsumer().accept(taskWrapper.task);
                }
                break;
            }
        }
    }


    /**
     * 模拟翻译执行
     */
    @Data
    public static class Task {

        String name = "";

        int sort = 0;

        boolean async = false;

        Task[] after;

        public Task(String name) {
            this.name = name;
        }
    }

    @Data
    @AllArgsConstructor
    public static class TaskWrapper {

        Task task;

        short index = 0;

        Consumer<Task> consumer;
    }


}
