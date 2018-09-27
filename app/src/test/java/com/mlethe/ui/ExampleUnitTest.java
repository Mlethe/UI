package com.mlethe.ui;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test() {
        List<Prize> coupons = new ArrayList<>();
        Prize coupon = new Prize(1200l, new BigDecimal(0.1), 1);
        Prize coupon1 = new Prize(1201l, new BigDecimal(0.10), 1);
        Prize coupon2 = new Prize(1202l, new BigDecimal(0.10), 1);
        Prize coupon3 = new Prize(1203l, new BigDecimal(0.10), 1);
        coupons.add(coupon);
        coupons.add(coupon1);
        coupons.add(coupon2);
        coupons.add(coupon3);

        int index = 0;
        int index1 = 0;
        int index2 = 0;
        int index3 = 0;
        int times = 1000;
        for (int i = 0; i < times; i++) {
            long id = pay(coupons);
            if (id == 1200l) {
                index ++;
                System.out.println("id->" + id);
            } else if (id == 1201l) {
                index1 ++;
            } else if (id == 1202l) {
                index2 ++;
            } else if (id == 1203l) {
                index3 ++;
            }
        }
        System.out.println("抽奖次数：" + times + "    获奖次数：" + index + "ELECTRONICPRODUCT");
        System.out.println("抽奖次数：" + times + "    获奖次数：" + index1);
        System.out.println("抽奖次数：" + times + "    获奖次数：" + index2);
        System.out.println("抽奖次数：" + times + "    获奖次数：" + index3);
    }

    // 放大倍数
    private static final int mulriple = 1000000;

    public long pay(List<Prize> prizes) {
        int lastScope = 0;
        // 洗牌，打乱奖品次序
        Collections.shuffle(prizes);
        Map<Long, int[]> prizeScopes = new HashMap<Long, int[]>();
        Map<Long, Integer> prizeQuantity = new HashMap<Long, Integer>();
        for (Prize prize : prizes) {
            Long prizeId = prize.getPrizeId();
            // 划分区间
            int currentScope = lastScope + prize.getProbability().multiply(new BigDecimal(mulriple)).intValue();
            prizeScopes.put(prizeId, new int[] { lastScope + 1, currentScope});
            prizeQuantity.put(prizeId, prize.getQuantity());

            lastScope = currentScope;
        }

        // 获取1-1000000之间的一个随机数
        int luckyNumber = new Random().nextInt(mulriple);
        long luckyPrizeId = 0;
        // 查找随机数所在的区间
        if ((null != prizeScopes) && !prizeScopes.isEmpty()) {
            Set<Map.Entry<Long, int[]>> entrySets = prizeScopes.entrySet();
            for (Map.Entry<Long, int[]> m : entrySets) {
                long key = m.getKey();
                if (luckyNumber >= m.getValue()[0] && luckyNumber <= m.getValue()[1] && prizeQuantity.get(key) > 0) {
                    luckyPrizeId = key;
                    break;
                }
            }
        }

        if (luckyPrizeId > 0) {
            // 奖品库存减一
        }

        return luckyPrizeId;
    }

    @Test
    public void test2() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
//        c.set(Calendar.HOUR_OF_DAY, n);
//        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        long time = c.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse("2018-09-26 13:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int i = c.get(Calendar.MINUTE);
        Date date1 = new Date((time / (1000 * 60 * 10)) * (1000 * 60 * 10));
        System.out.printf("first->" + sdf.format(new Date(time)) + "     second->" + "|" + (time % (1000 * 60 * 10) == 0) + "    third->" + (date.getTime() % (1000 * 60 * 10) == 0) + "     minute->" + i + "     x->" + (i / 10) * 10 + "   " + sdf.format(date1));
    }
}