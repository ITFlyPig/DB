package com.wyl.log.upload;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author : yuelinwang
 * @time : 6/9/21
 * @desc :
 */

@RunWith(AndroidJUnit4.class)
public class GlobalCountTest {

    @Test
    public void testAddAndGet() {
        String key = "key";
        // 生产线程
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
//                            long cur = GlobalCount.addAndGet(key, 1);
                            long cur = GlobalDBCount.addAndGet(1);
                            System.out.println("======生产，当前数量：" + cur + "========");
                        }

                    }
                }
        );
        t.start();

        // 消费线程
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
//                            long cur = GlobalCount.decrementAndGet(key, 1);
                            long cur = GlobalDBCount.decrementAndGet(1);
                            System.out.println("======消费，当前数量：" + cur + "========");
                        }

                    }
                }
        ).start();


        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}