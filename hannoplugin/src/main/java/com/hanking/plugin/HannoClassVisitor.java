package com.hanking.plugin;

import com.hanking.utils.LogHelper;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Vector;

/**
 * create by 胡汉君
 * date 2021/11/24 14：06
 */
public class HannoClassVisitor extends ClassVisitor {

    private String className;
    private static final String CN_LOG = "Lcom/hank/hannotation/HannoLog;";
    //是否打印全部方法的log，默认是false，只有类上加了cnLog才会打印全部的log
    private boolean logAll = false;
    //用于存放fieldInfo
    private final Vector<FieldInfo> fieldInfos = new Vector<>(10);

    public HannoClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
//        LogHelper.log(" visit name " + name + " superName " + superName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
        LogHelper.log("visitMethod logAll " + logAll);
        return new HannoMethodAdapter(api, methodVisitor, access, name, descriptor, className, logAll,fieldInfos);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
//        LogHelper.log(" visit attribute " + attribute.toString());
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        LogHelper.log("visitAnnotation descriptor " + descriptor + " visible " + visible);
        //获取类上的annotation，如果有类上的annotation，那么给每一个方法生成一个log
        //如果有cnLog那么其他的方法上的log就不再处理
        if (descriptor.equals(CN_LOG)) {
            //做一个处理
            logAll = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        Type type = Type.getType(descriptor);
        //判断是否是静态方法，如果是静态方法则不能去获取类中非field的值，如果不是静态方法则可以获取field的值
        LogHelper.log(" visit visitField access " + access + " name " + name + " descriptor " + descriptor + " signature " + signature + " value " + value + " type " + type);
        //用fieldInfo将当前的field存放起来
        fieldInfos.add(new FieldInfo(descriptor, access, type, name));
        return super.visitField(access, name, descriptor, signature, value);
    }
}