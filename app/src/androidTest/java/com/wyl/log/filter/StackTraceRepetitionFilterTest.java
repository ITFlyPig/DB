package com.wyl.log.filter;

import com.wyl.log.handle.LogConstant;
import com.wyl.log.handle.LogType;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Random;

/**
 * @author : yuelinwang
 * @time : 7/6/21
 * @desc : 测试
 */
public class StackTraceRepetitionFilterTest extends TestCase {

    public void testFilter() {
        ILogFilter logFilter = new StackTraceRepetitionFilter();
        for (int i = 0; i < 20; i++) {
            HashMap<String, String> logMap = new HashMap<>();
            logMap.put(LogConstant.LOG_TYPE, String.valueOf(LogType.CRASH));
            String content = "据刘向《说苑·善说》记载：春秋时代，楚王母弟鄂君子皙在河中游玩，钟鼓齐鸣。摇船者是位越人，趁乐声刚停，便抱双桨用越语唱了一支歌。鄂君子皙听不懂，叫人翻译成楚语。就是上面的歌谣。歌中唱出了越人对子皙的那种深沉真挚的爱恋之情，歌词 声义双关，委婉动听。是中国最早的译诗，也是古代楚越文化交融的结晶和见证。它对楚辞创作有着直接的影响作用。（选自《先秦诗文精华》 人民文学出版社2000.1版）\n";
            logMap.put(LogConstant.CONTENT, content);
            if (i == 0) {
                assertFalse(logFilter.filter(logMap));
            } else {
                // 模拟随机的放入到队列时间
                int random = new Random().nextInt(20);
                if (random >= 10) {
                    try {
                        Thread.sleep(random);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    assertFalse(logFilter.filter(logMap));
                } else {
                    try {
                        Thread.sleep(random);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    assertTrue(logFilter.filter(logMap));
                }

            }
        }
    }


}