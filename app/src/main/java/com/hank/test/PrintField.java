package com.hank.test;

import com.hank.hannotation.HannoLog;

public class PrintField {
    private final static int a = 2;
    boolean b = false;
    String s = "sss";
    float f = 1.0f;

    @HannoLog
    void test(int a) {
        System.out.println(a);
        System.out.println(b);
    }

}
