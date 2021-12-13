package com.hanking.asmdemo;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingMethodAdapter;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.Map;


/**
 * _                 _    _
 * | |               | |  (_)
 * | |__   __ _ _ __ | | ___ _ __   __ _
 * | '_ \ / _` | '_ \| |/ / | '_ \ / _` |
 * | | | | (_| | | | |   | | | | | (_| |
 * |_| |_|\__,_|_| |_|_|\_\_|_| |_|\__, |
 * ********************************* __/ |
 * ******************************** |___/
 * create time 2021/12/12 12:24 下午
 * create by 胡汉君
 */
public class AsmDemo {

    public class FieldAdder extends ClassVisitor {
        private final FieldNode fn;

        public FieldAdder(ClassVisitor cv, FieldNode fn) {
            super(Opcodes.ASM5, cv);
            this.fn = fn;
        }

        public void visitEnd() {
            fn.accept(cv);
            super.visitEnd();
        }
    }

    public class MethodAdder extends ClassVisitor {
        private int mAccess;
        private String mName;
        private String mDesc;
        private String mSignature;
        private String[] mExceptions;

        public MethodAdder(ClassVisitor cv, int mthAccess, String mthName, String mthDesc, String mthSignature, String[] mthExceptions) {
            super(Opcodes.ASM5, cv);
            this.mAccess = mthAccess;
            this.mName = mthName;
            this.mDesc = mthDesc;
            this.mSignature = mthSignature;
            this.mExceptions = mthExceptions;
        }

        public void visitEnd() {
            MethodVisitor mv = cv.visitMethod(mAccess, mName, mDesc, mSignature, mExceptions);
            // create method body     mv.visitMaxs(0, 0);     mv.visitEnd();     super.visitEnd();
        }
    }

    public class MethodReplacer extends ClassVisitor {
        private String mname;
        private String mdesc;
        private String cname;

        public MethodReplacer(ClassVisitor cv, String mname, String mdesc) {
            super(Opcodes.ASM5, cv);
            this.mname = mname;
            this.mdesc = mdesc;
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.cname = name;
            cv.visit(version, access, name, signature, superName, interfaces);
        }

        public MethodVisitor visitMethod(int access,
                                         String name, String desc,
                                         String signature, String[] exceptions) {
            String newName = name;
            if (name.equals(mname) && desc.equals(mdesc)) {
                newName = "orig$" + name;
                generateNewBody(access, desc, signature, exceptions, name, newName);
            }
            return super.visitMethod(access, newName, desc, signature, exceptions);
        }

        private void generateNewBody(int access,
                                     String desc, String signature,
                                     String[] exceptions,
                                     String name, String newName) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            // ...
            mv.visitCode();     // call original metod     mv.visitVarInsn(Opcodes.ALOAD, 0); // this     mv.visitMethodInsn(access, cname, newName,         desc);
            // ...     mv.visitEnd();
        }
    }

    public class MergeAdapter extends ClassVisitor {
        private ClassNode cn;
        private String cname;

        public MergeAdapter(ClassVisitor cv,
                            ClassNode cn) {
            super(Opcodes.ASM5, cv);
            this.cn = cn;
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.cname = name;
        }

        public void visitEnd() {
            for (Iterator it = cn.fields.iterator(); it.hasNext(); ) {
                ((FieldNode) it.next()).accept(this);
            }
            for (Iterator it = cn.methods.iterator(); it.hasNext(); ) {
                MethodNode mn = (MethodNode) it.next();
                String[] exceptions = new String[mn.exceptions.size()];
                mn.exceptions.toArray(exceptions);
                MethodVisitor mv = cv.visitMethod(mn.access, mn.name, mn.desc, mn.signature, exceptions);
                mn.instructions.resetLabels();
                mn.accept(new RemappingMethodAdapter(mn.access, mn.desc, mv, new SimpleRemapper(cname, cn.name)));
            }
            super.visitEnd();
        }
    }

    class EnteringAdapter extends AdviceAdapter {
        private String name;
        private Label timeVarStart = new Label();
        private Label timeVarEnd = new Label();


        /**
         * Constructs a new {@link AdviceAdapter}.
         *
         * @param api           the ASM API version implemented by this visitor. Must be one of {@link
         *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
         * @param methodVisitor the method visitor to which this adapter delegates calls.
         * @param access        the method's access flags (see {@link Opcodes}).
         * @param name          the method's name.
         * @param descriptor    the method's descriptor (see {@link Type Type}).
         */
        protected EnteringAdapter(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
            this.name = name;
        }

        protected void onMethodEnter() {
            visitLabel(timeVarStart);
            int timeVar = newLocal(Type.getType("J"));
            visitLocalVariable("timeVar", "J", null, timeVarStart, timeVarEnd, timeVar);
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava//io // PrintStream;");
            super.visitLdcInsn("Entering " + name);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                    "(Ljava/lang/String;)V");
        }

        public void visitMaxs(int stack, int locals) {
            visitLabel(timeVarEnd);
            super.visitMaxs(stack, locals);
        }
    }
}

