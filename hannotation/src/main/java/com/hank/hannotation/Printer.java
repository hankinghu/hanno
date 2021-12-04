package com.hank.hannotation;

import android.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * create by 胡汉君
 * date 2021/11/23 11：06
 * 用来处理log的工具类
 */
public class Printer {

    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "───────────────────────────────────------";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;

    private static final String METHOD_NAME_FORMAT = "%s method: %s";
    private static final String ARGUMENT_FORMAT = "%s params: ";
    private static final String RESULT_FORMAT = "%s result: ";
    private static final String COST_TIME_FORMAT = "%s time: %dms";
    private static final String RUN_THREAD_FORMAT = "%s thread: %s";
    private static final String FIELD_NAME_FORMAT = "%s fields: %s";

    /**
     * 此处添加同步，防止多线程调用是log打印错误
     */
    public static synchronized void printMethodInfo(MethodInfo methodInfo, int level, boolean enableTime, String tagName, Vector<FieldInfoN> fieldInfoNS, boolean watchStack) {
        String TAG = tagName.isEmpty() ? methodInfo.getClassName() : tagName;
        Log.println(level, TAG, TOP_BORDER);
        Log.println(level, TAG, String.format(METHOD_NAME_FORMAT, HORIZONTAL_LINE, methodInfo.getMethodName()));
        if (!methodInfo.getArgumentList().isEmpty()) {
            Log.println(level, TAG, String.format(ARGUMENT_FORMAT, HORIZONTAL_LINE) + methodInfo.getArgumentList());
        }
        if (methodInfo.getResult() != null) {
            Log.println(level, TAG, String.format(RESULT_FORMAT, HORIZONTAL_LINE) + methodInfo.getResult());
        }
        if (enableTime) {
            Log.println(level, TAG, String.format(Locale.CHINA, COST_TIME_FORMAT, HORIZONTAL_LINE, methodInfo.getCost()));
        }
        /**
         * 打印参数值 将参数链接起来打印
         */
        StringBuilder fields = new StringBuilder();
        for (FieldInfoN infoN : fieldInfoNS) {
            fields.append(infoN.toString());
        }
        if (!fieldInfoNS.isEmpty()) {
            Log.println(level, TAG, String.format(Locale.CHINA, FIELD_NAME_FORMAT, HORIZONTAL_LINE, fields.toString()));
        }
        Log.println(level, TAG, String.format(Locale.CHINA, RUN_THREAD_FORMAT, HORIZONTAL_LINE, Thread.currentThread().getName()));
        Log.println(level, TAG, BOTTOM_BORDER);
        //watch stack,打印方法的调用栈
        if (watchStack) {
            Log.println(level, TAG, Log.getStackTraceString(new Throwable()));
        }
    }
}
