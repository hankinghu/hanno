package com.hanking.plugin;

import static com.hanking.plugin.Constants.ANNOTATION_NAME;

import com.hanking.utils.LogHelper;
import com.hanking.utils.VariableCache;
import com.hanking.utils.VariableEntry;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * _                 _    _
 * | |               | |  (_)
 * | |__   __ _ _ __ | | ___ _ __   __ _
 * | '_ \ / _` | '_ \| |/ / | '_ \ / _` |
 * | | | | (_| | | | |   | | | | | (_| |
 * |_| |_|\__,_|_| |_|_|\_\_|_| |_|\__, |
 * ********************************* __/ |
 * ******************************** |___/
 * create time 2021/12/2 5:33 下午
 * create by 胡汉君
 * 用于遍历获取参数信息的visitor
 */
public class VariableMethodVisitor extends AdviceAdapter {
    //是否需要存储方法的变量，默认是false
    private boolean needSave;
    private final String className;
    private final String methodName;
    protected VariableMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, boolean needSave, String className) {
        super(api, methodVisitor, access, name, descriptor);
        this.needSave = needSave;
        this.className = className;
        this.methodName=name;
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        //遍历localVariable
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
        //存放方法的参数值
//        LogHelper.log("visitLocalVariable name " + name + " descriptor " + descriptor + " index " + index);
        if (needSave && !name.equals("this")) {
            //先全部存放起来,key是className_methodName
            VariableCache.setVariable(className + "_" + methodName + "_" + index, new VariableEntry(name, descriptor));
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        //遍历注解
        if (descriptor.equals(ANNOTATION_NAME)) {
            needSave = true;
        }
        return super.visitAnnotation(descriptor, visible);

    }
}
