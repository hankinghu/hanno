package com.hanking.utils;

/**
 * _                 _    _
 * | |               | |  (_)
 * | |__   __ _ _ __ | | ___ _ __   __ _
 * | '_ \ / _` | '_ \| |/ / | '_ \ / _` |
 * | | | | (_| | | | |   | | | | | (_| |
 * |_| |_|\__,_|_| |_|_|\_\_|_| |_|\__, |
 * ********************************* __/ |
 * ******************************** |___/
 * create time 2021/12/2 5:41 下午
 * create by 胡汉君
 * 用于存放
 */
public class VariableEntry {
    //名字
    String name;
    //描述
    String desc;

    public VariableEntry(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "VariableEntry{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }
}
