package com.hanking.utils;

public class LogHelper {
    private static boolean openLog = false;

    /**
     * @param message 用于打印log,可以控制只有调试的时候再打印log，不调试的时候可以通过设置openLog为false来不打印log
     */
    public static void log(String message) {
        if (openLog) {
            System.out.println(message);
        }
    }

}
