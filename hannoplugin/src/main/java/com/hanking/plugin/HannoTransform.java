package com.hanking.plugin;

import static org.objectweb.asm.ClassReader.SKIP_FRAMES;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.hanking.extension.HannoExtension;
import com.hanking.utils.LogHelper;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * create by 胡汉君
 * date 2021/11/24 14：07
 * transform用于处理asm
 */
public class HannoTransform extends Transform {
    private final HannoExtension hannoExtension;

    public HannoTransform(HannoExtension hannoExtension) {
        this.hannoExtension = hannoExtension;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws IOException {
        LogHelper.log("trace transform begin ... is enable " + hannoExtension.isEnable() + " hannoExtension.isOpenLog() " + hannoExtension.isOpenLog());
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        if (!transformInvocation.isIncremental()) {
            outputProvider.deleteAll();
        }
        //输入数据
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //遍历input列表
        for (TransformInput input : inputs) {
            //获取目录的inputs
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            for (DirectoryInput dirInput : directoryInputs) {
//                LogHelper.log();("dirInput name ..." + dirInput.getName());
                File inputFile = dirInput.getFile();
                //只有在enable的时候才去处理dir文件，否则直接返回
                if (inputFile.isDirectory() && hannoExtension.isEnable()) {
                    //遍历一下
                    handleDir(inputFile);
                }
                File outputFile = outputProvider.getContentLocation(dirInput.getName(), dirInput.getContentTypes(), dirInput.getScopes(), Format.DIRECTORY);
                FileUtils.copyDirectory(inputFile, outputFile);
            }
            //获取jar包的inputs
            Collection<JarInput> jarInputs = input.getJarInputs();
            for (JarInput jarInput : jarInputs) {
                File inputFile = jarInput.getFile();
                File outputFile = outputProvider.getContentLocation(jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                FileUtils.copyFile(inputFile, outputFile);
            }
        }
        LogHelper.log("trace transform end ...");

    }

    private void handleDir(File inputFile) throws IOException {
        File[] files = inputFile.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                handleDir(file);
            } else {
                if (checkClassFile(file.getName())) {
                    //第一次遍历只获取变量相关的信息
                    byte[] code = getBytes(file);
                    //第二次遍历进行修改
                    byte[] code1 = travel(code);
                    FileOutputStream fos = new FileOutputStream(
                            file.getParentFile().getAbsolutePath() + File.separator + file.getName());
                    try {
                        fos.write(code1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos.close();
                }
                //如果是文件就处理
            }
        }
    }

    private byte[] travel(byte[] code) {
        ClassReader classReader = new ClassReader(code);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new HannoClassVisitor(Opcodes.ASM5, classWriter);
        classReader.accept(classVisitor, SKIP_FRAMES);
        return classWriter.toByteArray();
    }

    /**
     * @param file 文件名
     * @return 返回byte数组
     * @throws IOException 异常
     */
    private byte[] getBytes(File file) throws IOException {
        ClassReader classReader = new ClassReader(new FileInputStream(file));
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new VariableClassVisitor(classWriter);
        classReader.accept(cv, SKIP_FRAMES);
        return classWriter.toByteArray();
    }

    boolean checkClassFile(String name) {
        //只处理需要的class文件
        return (name.endsWith(".class") && !name.startsWith("R\\$")
                && !"R.class".equals(name) && !"BuildConfig.class".equals(name));
    }

    @Override
    public String getName() {
        return "traceForm";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

}
