package com.hanking.extension;

/**
 * _                 _    _
 * | |               | |  (_)
 * | |__   __ _ _ __ | | ___ _ __   __ _
 * | '_ \ / _` | '_ \| |/ / | '_ \ / _` |
 * | | | | (_| | | | |   | | | | | (_| |
 * |_| |_|\__,_|_| |_|_|\_\_|_| |_|\__, |
 * ********************************* __/ |
 * ******************************** |___/
 * create time 2021/12/1 2:32 下午
 * create by 胡汉君
 * 创建一个extension，用于控制是否执行log的字节码插装
 */
public class HannoExtension {
    //控制是否使用Hanno
    boolean enable = false;
    //控制是否打印log
    boolean openLog = true;

    public boolean isEnable() {
        return enable;
    }

    public boolean isOpenLog() {
        return openLog;
    }

    public void setOpenLog(boolean openLog) {
        this.openLog = openLog;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
