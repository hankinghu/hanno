package com.hanking.plugin;


import static com.hanking.plugin.Constants.ANNOTATION_NAME;
import static com.hanking.plugin.Constants.LOG_CACHE_NAME;

import com.hanking.utils.LogHelper;
import com.hanking.utils.VariableCache;
import com.hanking.utils.VariableEntry;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.Vector;
/**
 * create by 胡汉君
 * date 2021/11/24 14：05
 */

/**
 * 用于判断方法进入和方法退出,静态内部类，可以获取外部类的变量
 */
class HannoMethodAdapter extends AdviceAdapter {
    private boolean hasTraceLog = false;
    private final String methodName;
    private int methodId;
    private final boolean isStaticMethod;
    private int startTimeId;
    private final Type[] argumentArrays;
    private final String desc;
    private int level = 3;
    private boolean enableTime = false;
    private final String className;
    private boolean logAll = false;
    private String tagName = "";
    private final Vector<FieldInfo> fieldInfos;
    private boolean watchField = false;
    private boolean watchStack = false;

    protected HannoMethodAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String className, boolean logAll, Vector<FieldInfo> fieldInfos) {
        super(api, methodVisitor, access, name, descriptor);
        this.methodName = name;
        this.desc = descriptor;
        argumentArrays = Type.getArgumentTypes(desc);
        //定义一下方法的名称列表长度
        isStaticMethod = ((access & Opcodes.ACC_STATIC) != 0);
        this.className = className;
        this.logAll = logAll;
        this.fieldInfos = fieldInfos;
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        if (methodName.equals("<init>()")) {
            return;
        }
        if (hasTraceLog || logAll) {
            methodId = newLocal(Type.INT_TYPE);
            mv.visitMethodInsn(INVOKESTATIC, LOG_CACHE_NAME, "request", "()I", false);
            mv.visitIntInsn(ISTORE, methodId);
            addArgument();
            startTimeId = newLocal(Type.LONG_TYPE);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitIntInsn(LSTORE, startTimeId);
        }
        LogHelper.log(" onMethodEnter name " + className + " hasTraceLog " + hasTraceLog);
    }

    /**
     * 添加参数
     */
    private void addArgument() {
        for (int i = 0; i < argumentArrays.length; i++) {
            Type type = argumentArrays[i];
            int index = isStaticMethod ? i : (i + 1);
            int opcode = type.getOpcode(Opcodes.ILOAD);
            mv.visitVarInsn(opcode, index);
            box(type);
            mv.visitVarInsn(ILOAD, methodId);
            //通过class_method获取方法参数
            VariableEntry entry = VariableCache.getVariable(className + "_" + methodName + "_" + index);
            if (entry != null) {
                LogHelper.log("onMethodEnter i " + i + " type " + type + " argumentNames " + entry.toString());
            }
            if (entry == null) {
                mv.visitLdcInsn("null");
            } else {
                mv.visitLdcInsn(entry.getName());
            }
            visitMethodInsn(INVOKESTATIC, LOG_CACHE_NAME, "addMethodArgument",
                    "(Ljava/lang/Object;ILjava/lang/String;)V", false);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
//        LogHelper.log();("visitAnnotation descriptor " + descriptor + " visible " + visible + " onMethod name " + methodName);
        if (descriptor.equals(ANNOTATION_NAME)) {
            hasTraceLog = true;
        }
//        LogHelper.log();("visitAnnotation hasTraceLog -------111------ " + hasTraceLog);
        return new HannoAnnotationVisitor(super.visitAnnotation(descriptor, visible));
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if (methodName.equals("<init>")) {
            return;
        }
//        LogHelper.log("onMethodExit hasTraceLog " + hasTraceLog);
        if (hasTraceLog || logAll) {

            //保存处理一下field类型的参数值
            handleFieldInfos();

            if (opcode == RETURN) {
                visitInsn(ACONST_NULL);
            } else if (opcode == ARETURN || opcode == ATHROW) {
                dup();
            } else {
                if (opcode == LRETURN || opcode == DRETURN) {
                    dup2();
                } else {
                    dup();
                }
                box(Type.getReturnType(this.methodDesc));
            }
            mv.visitLdcInsn(className);
            mv.visitLdcInsn(methodName);
            mv.visitLdcInsn(desc);
            mv.visitVarInsn(LLOAD, startTimeId);
            mv.visitVarInsn(ILOAD, methodId);
            mv.visitMethodInsn(INVOKESTATIC, LOG_CACHE_NAME, "updateMethodInfo",
                    "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JI)V", false);

            mv.visitVarInsn(ILOAD, methodId);
//            LogHelper.log("methodId------- " + methodId);
            mv.visitLdcInsn(level);
            mv.visitLdcInsn(enableTime);
            mv.visitLdcInsn(tagName);
            mv.visitLdcInsn(watchStack);
            mv.visitMethodInsn(INVOKESTATIC, LOG_CACHE_NAME,
                    "printMethodInfo", "(IIZLjava/lang/String;Z)V", false);

        }
    }


    /**
     * 处理fieldInfo相关信息
     */
    private void handleFieldInfos() {
        //不是静态方法才获取，如果是静态方法则不获取
        if (watchField && !fieldInfos.isEmpty() && !isStaticMethod) {
            for (FieldInfo info : fieldInfos) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, className, info.name, info.descriptor);
                //进行一下装箱操作，不然会包类型转换错误
                box(info.type);
                mv.visitLdcInsn(info.name);
                mv.visitLdcInsn(info.descriptor);
                mv.visitMethodInsn(INVOKESTATIC, LOG_CACHE_NAME,
                        "setFieldValues", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);
//                LogHelper.log("handleFieldInfos " + "store code " + info.getStoreCode() + " load code " + info.getLoadCode());
            }
        }
    }

    class HannoAnnotationVisitor extends AnnotationVisitor {

        public HannoAnnotationVisitor(AnnotationVisitor annotationVisitor) {
            super(Opcodes.ASM5, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
//            LogHelper.log("HannoAnnotationVisitor visit name " + name + " value " + value + " className " + className);
            switch (name) {
                case "level":
                    level = Integer.parseInt(value.toString());
                    break;
                case "enableTime":
                    enableTime = Boolean.parseBoolean(value.toString());
                    break;
                case "tagName":
                    tagName = String.valueOf(value);
                    break;
                case "watchField":
                    watchField = Boolean.parseBoolean(value.toString());
                case "watchStack":
                    watchStack = Boolean.parseBoolean(value.toString());

            }
            super.visit(name, value);
        }
    }
}