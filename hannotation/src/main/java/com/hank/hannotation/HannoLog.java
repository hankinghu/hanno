package com.hank.hannotation;

import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * create by 胡汉君
 * date 2021/11/10 17：38
 * 定义一个注解，用于标注当前方法需要打印log
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface HannoLog {
    //定义一下log的级别，默认是3，debug级别
    int level() default Log.DEBUG;

    /**
     * @return 打印方法的运行时间
     */
    boolean enableTime() default false;

    /**
     * @return tag的名称，默认是类名，也可以设置
     */
    String tagName() default "";

    /**
     * @return 是否观察field的值，如果观察就会就拿到对象里面全部的field值
     */
    boolean watchField() default false;

    /**
     *
     * @return 是否观察方法的调用栈
     */
    boolean watchStack() default false;

}
