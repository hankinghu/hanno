package com.hank.hannotation;

import java.util.Vector;

/**
 * create by 胡汉君
 * date 2021/11/23 11：06
 * 用来处理log的工具类
 */
public class LogCache {
    /**
     * 方法缓存默认大小
     */
    private static final int INIT_CACHE_SIZE = 1024;
    /**
     * 方法名缓存
     */
    private static Vector<MethodInfo> mCacheMethods = new Vector<>(INIT_CACHE_SIZE);
    private static Vector<FieldInfoN> mCacheFields = new Vector<>(20);

    /**
     * 占位并生成方法ID
     *
     * @return 返回 方法 Id
     */
    public static int request() {
        mCacheMethods.add(new MethodInfo());
        return mCacheMethods.size() - 1;
    }

    public static void addMethodArgument(Object argument, int id, String name) {
        MethodInfo methodInfo = mCacheMethods.get(id);
        methodInfo.addArgument(new MethodInfo.AgNode(name, argument));
    }

    public static void updateMethodInfo(Object result, String className, String methodName, String methodDesc, long startTime, int id) {
        MethodInfo methodInfo = mCacheMethods.get(id);
        methodInfo.setCost((System.currentTimeMillis() - startTime));
        methodInfo.setResult(result);
        methodInfo.setMethodDesc(methodDesc);
        methodInfo.setClassName(className);
        methodInfo.setMethodName(methodName);
    }

    public static synchronized void printMethodInfo(int id, int logLevel, boolean enableTime, String tagName,boolean watchStack) {
        MethodInfo methodInfo = mCacheMethods.get(id);
        Printer.printMethodInfo(methodInfo, logLevel, enableTime, tagName, mCacheFields,watchStack);
    }

    /**
     * @param fieldValues field值
     *                    设置field的值
     *                    以及field名称
     */
    public static void setFieldValues(Object fieldValues, String name, String descriptor) {
        //如果field值没有变化则不加,防止添加多了field
        FieldInfoN infoN=new FieldInfoN(descriptor, name, fieldValues);
        if (!mCacheFields.contains(infoN)) {
            mCacheFields.add(new FieldInfoN(descriptor, name, fieldValues));
        }
    }

}
