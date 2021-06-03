package com.wyl.log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.wyl.db.util.LogUtil;
import com.wyl.log.filter.ILogFilter;
import com.wyl.log.filter.StackTraceRepetitionFilter;
import com.wyl.log.handle.LogConstant;
import com.wyl.log.handle.LogType;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

/**
 * @author : yuelinwang
 * @time : 2021/6/3
 * @desc :
 */
@RunWith(AndroidJUnit4.class)
public class StackTraceRepetitionFilterTest {
    public static final String TAG = StackTraceRepetitionFilterTest.class.getSimpleName();


    @Test
    public void testFilter() {
        ILogFilter filter = new StackTraceRepetitionFilter();


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    String test1 = "据刘向《说苑·善说》记载：春秋时代，楚王母弟鄂君子皙在河中游玩，钟鼓齐鸣。摇船者是位越人，趁乐声刚停，便抱双桨用越语唱了一支歌。鄂君子皙听不懂，叫人翻译成楚语。就是上面的歌谣。歌中唱出了越人对子皙的那种深沉真挚的爱恋之情，歌词 声义双关，委婉动听。是中国最早的译诗，也是古代楚越文化交融的结晶和见证。它对楚辞创作有着直接的影响作用。（选自《先秦诗文精华》 人民文学出版社2000.1版）\n";
                    String test2 = "据刘向《说苑·善说》记载：春秋时代，楚王母弟鄂君子皙在河中游玩，钟鼓齐鸣。摇船者是位越人，趁乐声刚停，便抱双桨用越语唱了一支歌。鄂君子皙听不懂，叫人翻译成楚语。就是上面的歌谣。歌中唱出了越人对子皙的那种深沉真挚的爱恋之情，歌词 声义双关，委婉动听。是中国最早的译诗，也是古代楚越文化交融的结晶和见证。它对楚辞创作有着直接的影响作用。（选自《先秦诗文精华》 人民文学出版社2000.1版）\n";
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(LogConstant.LOG_TYPE, String.valueOf(LogType.CRASH));
                    if (count % 2 == 0) {
                        hashMap.put(LogConstant.CONTENT, test1);
                    } else {
                        hashMap.put(LogConstant.CONTENT, test2);
                    }
                    count++;
                    LogUtil.w(TAG, "是否过滤：" + filter.filter(hashMap));

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}