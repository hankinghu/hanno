# hanno
 ```java
_    _
| |  | |                        
| |__| | __ _ _ __  _ __   ___  
|  __  |/ _` | '_ \| '_ \ / _ \
| |  | | (_| | | | | | | | (_) |
|_|  |_|\__,_|_| |_|_| |_|\___/
```
通过字节码插件实现注解打印log，注解可以加在类上面，也可以加在方法上面，当加在类上面时会打印全部方法的log，当加在方法上面时打印当前方法的log

# 使用方法
1、给类中所有的方法加上log
```java
@HannoLog
class MainActivity : AppCompatActivity() {
   // ...
}
```
会打印出类中所有方法的log

2、给类中的某些方法加log
```java
class MainActivity : AppCompatActivity() {
    @HannoLog(level = Log.INFO, enableTime = false,watchField=true)
    private fun test(a: Int = 3, b: String = "good"): Int {
        return a + 1
    }
}
```
会打印当前方法的log
3、打印的log
```java
//D/MainActivity: ┌───────────────────────────────────------───────────────────────────────────------
//D/MainActivity: │ method: onCreate(android.os.Bundle)
//D/MainActivity: │ params: [{name='savedInstanceState', value=null}]
//D/MainActivity: │ time: 22ms
//D/MainActivity: │ fields: {name='a', value=3}{name='b', value=false}{name='c', value=ccc}
//D/MainActivity: │ thread: main
//D/MainActivity: └───────────────────────────────────------───────────────────────────────────------
```
其中method是当前方法名，params是方法的参数名和值，time方法的执行时间，fields是当前对象的fields值，thread当前方法执行的线程。
# HannoLog参数解释

可以通过level来设置log的级别，level的设置可以调用Log里面的INFO，DEBUG，ERROR等。enableTime用来设置是否打印方法执行的时间，默认是false，如果要打印设置enableTime=true.
tagName用于设置log的名称，默认是当前类名，也可以通过这个方法进行设置。

1、level控制log打印的等级，默认是log.d,可以通过@HannoLog(level = Log.INFO)来设置等级，支持Log.DEBUG，Log.ERROR等。

2、enableTime控制是否输出方法的执行时间，默认是false，如果要打印可以通过@HannoLog(enableTime=true)来设置。

3、tagName设置tag的名称，默认是当前类名，也可以通过    @HannoLog(tagName = "test")来设置。

4、watchField用于观察对象中的field值，通过@HannoLog(watchField = true)设置，由于静态方法中不能调用非静态的field所以这个参数在静态方法上统一不生效。

# 重要的类
1、HannoLog
HannoLog是注解类，里面提供了控制参数。对应上面的HannoLog参数解释

 ```java
/**
 * 
 * 
 * 
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
}
```
2、HannoExtension
 ```java
public class HannoExtension {
//控制是否使用Hanno
boolean enable;
//控制是否打印log
boolean openLog = true;

    public boolean isEnableModule() {
        return enableModule;
    }

    public void setEnableModule(boolean enableModule) {
        this.enableModule = enableModule;
    }

    //设置这个值为true可以给整个module的方法增加log
    boolean enableModule = false;

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
```
HannoExtension提供gradle.build文件是否开启plugin 和打印执行plugin的log
默认情况下添加HannoLog之后会进行asm插装，也可以通过在module的build.gradle文件中添加以下配置使在编译时不执行字节码插装提高编译速度
```java
apply plugin: 'com.hanking.hanno'
hannoExtension{
 enable=false
 openLog=false
}
```
# 工具调试类

1、ASM打印工具类 ASMPrint
```java
public class ASMPrint {
    
    public static void printAsm(String className) throws IOException {
        // (1) 设置参数

        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;

        // (2) 打印结果
        Printer printer = new ASMifier();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        new ClassReader(className).accept(traceClassVisitor, parsingOptions);
    }
}
```
通过ASMPrint类可以打印出class的asm代码，方便在Android studio中使用ByteCodeOutLine时出现问题。

2、查看complied class

运行字节码插桩工具后，可以在app/build/intermediates/transforms/目录下找到生成的class，但是class中的内容由于是compiled看不到，可以使用cfr工具支持反编译class文件和jar包
下载cfr的jar包，地址https://www.benf.org/other/cfr/,放到电脑的目录下，然后调用下面的命令就可以了
反编译class文件：命令
java -jar  /Users/hanking/AndroidStudioProjects/Honno/cfr/cfr-0.151.jar  MainActivity.class

插桩后生成的代码
```java
    @HannoLog(tagName="test", watchField=true)
    protected void onResume() {
        int n = LogCache.request();
        long l = System.currentTimeMillis();
        super.onResume();
        LogCache.setFieldValues((Object)new Integer(this.a), (String)"a", (String)"I");
        LogCache.setFieldValues((Object)new Boolean(this.b), (String)"b", (String)"Z");
        LogCache.setFieldValues((Object)this.c, (String)"c", (String)"Ljava/lang/String;");
        LogCache.updateMethodInfo(null, (String)"com/hank/hanno/MainActivity", (String)"onResume", (String)"()V", (long)l, (int)n);
        LogCache.printMethodInfo((int)n, (int)3, (boolean)false, (String)"test");
    }
```
# 实现原理
asm字节码原理

# 项目中依赖
在项目的build.gradle中添加
``` groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
    maven { url 'https://jitpack.io' }
  }
  dependencies {
    classpath "io.github.hankinghu:hannoplugin:1.1.5"
    
  }
}
```

在需要依赖的子module的build.gradle中添加

```groovy
apply plugin: "io.github.hankinghu.plugin"
implementation 'com.github.hankinghu:hanno:1.1.5'
```

