package com.hanking.plugin;
import com.android.build.gradle.AppExtension;
import com.hanking.utils.LogHelper;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * create by 胡汉君
 * date 2021/11/6 23：18
 * tracePlugin
 */
public class HannoPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        //实现一下这个project
        LogHelper.log("trace plugin start " + project.getName());
        project.getExtensions().findByType(AppExtension.class)
                .registerTransform(new HannoTransform());
    }
}




