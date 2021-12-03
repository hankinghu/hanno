package com.hanking.plugin;

import static com.hanking.plugin.Constants.ANNOTATION_NAME;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * _                 _    _
 * | |               | |  (_)
 * | |__   __ _ _ __ | | ___ _ __   __ _
 * | '_ \ / _` | '_ \| |/ / | '_ \ / _` |
 * | | | | (_| | | | |   | | | | | (_| |
 * |_| |_|\__,_|_| |_|_|\_\_|_| |_|\__, |
 * ********************************* __/ |
 * ******************************** |___/
 * create time 2021/12/2 5:21 下午
 * create by 胡汉君
 * 用来遍历variable的，获取方法的参数值和参数名
 */
public class VariableClassVisitor extends ClassVisitor {
    /**
     * 这个类只用来获取variable相关的数据
     */
    private String owner;
    private boolean needTravel = false;
    public VariableClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5,classVisitor);
    }

    //遍历注解
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor.equals(ANNOTATION_NAME)) {
            //拿到是否有注解，有注解才遍历，否则不遍历
            needTravel = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        owner=name;
    }

    //遍历方法
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        //如果需要遍历，这个时候进行遍历
        MethodVisitor methodVisitor=super.visitMethod(access, name, descriptor, signature, exceptions);
        return new VariableMethodVisitor(Opcodes.ASM5,methodVisitor,access,name,descriptor,needTravel,owner);
    }
}
