package com.hanyahunya.gitRemind;

import com.hanyahunya.gitRemind.contribution.util.AlarmTimeBitConverter;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(20);
        list.add(21);
        list.add(22);
        int i = AlarmTimeBitConverter.hourToBit(list);
        System.out.println(i);
    }
}
