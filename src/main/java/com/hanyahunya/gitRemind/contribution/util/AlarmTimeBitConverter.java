package com.hanyahunya.gitRemind.contribution.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AlarmTimeBitConverter {

    public static Set<Integer> bitToHourSet(int alarmBitValue) {
        Set<Integer> activeHours = new TreeSet<>();

        for (int i = 0; i < 23; i++) {
            if ((alarmBitValue & (1 << i)) != 0) {
                activeHours.add(i + 1); // 1時から23時まで
            }
        }
        return activeHours;
    }

    public static int hourToBit(Collection<Integer> hours) {
        int alarmBitValue = 0;

        for (int hour : hours) {
            if (hour < 1 || hour > 23) {
                continue;
            }
            alarmBitValue |= (1 << (hour - 1)); // 該当する時間にビット設定
        }

        return alarmBitValue;
    }
}