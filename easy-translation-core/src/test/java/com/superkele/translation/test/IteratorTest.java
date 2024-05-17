package com.superkele.translation.test;

import cn.hutool.core.collection.ListUtil;
import org.junit.Test;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class IteratorTest {

    @Test
    public void test() {
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>(ListUtil.of(1, 2, 3, 4, 5, 6, 7));
        CountDownLatch countDownLatch = new CountDownLatch(2);
        CompletableFuture.runAsync(()->{
            Iterator<Integer> iterator = queue.iterator();
            while (iterator.hasNext()){
                Integer next = iterator.next();
                iterator.remove();
                System.out.println("a_"+next);
            }
            countDownLatch.countDown();
        });
        CompletableFuture.runAsync(()->{
            Iterator<Integer> iterator = queue.iterator();
            while (iterator.hasNext()){
                Integer next = iterator.next();
                iterator.remove();
                System.out.println("b_"+next);
            }
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
