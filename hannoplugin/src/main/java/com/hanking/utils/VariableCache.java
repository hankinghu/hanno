package com.hanking.utils;

import java.util.HashMap;

/**
 * _                 _    _
 * | |               | |  (_)
 * | |__   __ _ _ __ | | ___ _ __   __ _
 * | '_ \ / _` | '_ \| |/ / | '_ \ / _` |
 * | | | | (_| | | | |   | | | | | (_| |
 * |_| |_|\__,_|_| |_|_|\_\_|_| |_|\__, |
 * ********************************* __/ |
 * ******************************** |___/
 * create time 2021/12/2 5:40 下午
 * create by 胡汉君
 * 用于存放variable的数据
 */
public class VariableCache {
    //参数值和参数名，参数类型，类名+方法名
    private static final HashMap<String, VariableEntry> hashMap = new HashMap<>(10);

    public static void setVariable(String key, VariableEntry entry) {
        LogHelper.log("setVariable---------- key " + key + " VariableEntry " + entry.toString());
        hashMap.put(key, entry);
    }

    public static VariableEntry getVariable(String key) {
        LogHelper.log("getVariable -----------key " + key);
        return hashMap.remove(key);
    }
}
