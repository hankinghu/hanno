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

# HannoLog
这个注解用来添加在类或者方法上，注解可以设置log的级别，是否打印方法运行时间，已经log的Tag名称


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
# 使用方法
1、注解添加在方法上面，所有添加注解的方法会打印log
```java
class MainActivity : AppCompatActivity() {

    @HannoLog(level = Log.DEBUG, enableTime = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    @HannoLog(tagName = "test")
    override fun onResume() {
        super.onResume()
    }

    @HannoLog(level = Log.INFO, enableTime = false)
    private fun test(a: Int = 3, b: String = "good"): Int {
        return a + 1
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

```
打印的log内容如下：
```java
/*
2021-11-29 11:07:26.591 29307-29307/com.hank.hanno D/test: ┌───────────────────────────────────------───────────────────────────────────------
2021-11-29 11:07:26.591 29307-29307/com.hank.hanno D/test: │ 执行的方法为: onResume()
2021-11-29 11:07:26.591 29307-29307/com.hank.hanno D/test: │ 方法的参数值: []
2021-11-29 11:07:26.592 29307-29307/com.hank.hanno D/test: │ {descriptor='I', name='a', value=3}
2021-11-29 11:07:26.592 29307-29307/com.hank.hanno D/test: │ {descriptor='Z', name='b', value=false}
2021-11-29 11:07:26.592 29307-29307/com.hank.hanno D/test: │ {descriptor='Ljava/lang/String;', name='c', value=ccc}
2021-11-29 11:07:26.594 29307-29307/com.hank.hanno D/test: │ 方法运行的线程为: main
2021-11-29 11:07:26.594 29307-29307/com.hank.hanno D/test: └───────────────────────────────────------───────────────────────────────────------
*/
```
![图片](https://github.com/hankinghu/hanno/raw/master/pic/log1.png)
![图片](https://github.com/hankinghu/hanno/raw/master/pic/log2.png)

# HannoLog参数解释
可以通过level来设置log的级别，level的设置可以调用Log里面的INFO，DEBUG，ERROR等。enableTime用来设置是否打印方法执行的时间，默认是false，如果要打印设置enableTime=true.
tagName用于设置log的名称，默认是当前类名，也可以通过这个方法进行设置。
1、level控制log打印的等级，默认是log.d,可以通过@HannoLog(level = Log.INFO)来设置等级，支持Log.DEBUG，Log.ERROR等。

2、enableTime控制是否输出方法的执行时间，默认是false，如果要打印可以通过@HannoLog(enableTime=true)来设置。

3、tagName设置tag的名称，默认是当前类名，也可以通过    @HannoLog(tagName = "test")来设置。

4、watchField用于观察对象中的field值，通过@HannoLog(watchField = true)设置，由于静态方法中不能调用非静态的field所以这个参数在静态方法上统一不生效。


2、注解添加在类上面
```java
@HannoLog
class MainActivity : AppCompatActivity() {

    @HannoLog(level = Log.DEBUG, enableTime = true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    @HannoLog(tagName = "test")
    override fun onResume() {
        super.onResume()
    }

    @HannoLog(level = Log.INFO, enableTime = false)
    private fun test(a: Int = 3, b: String = "good"): Int {
        return a + 1
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
```
类中所有的方法都会打印出log

![图片](https://github.com/hankinghu/hanno/raw/master/pic/log3.png)

# 添加依赖
在项目的build.gradle文件中添加
classpath "com.hanking.hanno:hannoPlugin:0.0.1-alpha.0"
在module中添加
apply plugin: 'com.hanking.hanno'
implementation "com.hanking.hanno:hannotation:0.0.1-alpha.0"
# Asm原理
1、ASM简介
一个.java文件经过Java编译器（javac）编译之后会生成一个.class文件。 在.class文件中，存储的是字节码（ByteCode）数据，如下图所示。

![图片](https://github.com/hankinghu/hanno/raw/master/pic/asm1.png)


ASM所的操作对象是是字节码（ByteCode）的类库。ASM处理字节码（ByteCode）数据的思路是这样的：第一步，将.class文件拆分成多个部分；第二步，对某一个部分的信息进行修改；第三步，将多个部分重新组织成一个新的.class文件。

ClassFile
```java
ClassFile {
u4             magic;
u2             minor_version;
u2             major_version;
u2             constant_pool_count;
cp_info        constant_pool[constant_pool_count-1];
u2             access_flags;
u2             this_class;
u2             super_class;
u2             interfaces_count;
u2             interfaces[interfaces_count];
u2             fields_count;
field_info     fields[fields_count];
u2             methods_count;
method_info    methods[methods_count];
u2             attributes_count;
attribute_info attributes[attributes_count];
}
```
字节码的类库和ClassFile之间关系

![图片](https://github.com/hankinghu/hanno/raw/master/pic/asm2.png)


ASM能够做什么

![图片](https://github.com/hankinghu/hanno/raw/master/pic/asm3.png)

asm的组成
从组成结构上来说，ASM分成两部分，一部分为Core API，另一部分为Tree API。
- 其中，Core API包括asm.jar、asm-util.jar和asm-commons.jar；
- 其中，Tree API包括asm-tree.jar和asm-analysis.jar。

  ![图片](https://github.com/hankinghu/hanno/raw/master/pic/asm4.png)

asm中比较重要的类

![图片](https://github.com/hankinghu/hanno/raw/master/pic/asm5.png)

# ASM打印工具类 ASMPrint
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

# 基础方法
1、获取对象中的field值
```java
mv.visitVarInsn(ALOAD, 0);
mv.visitFieldInsn(GETFIELD, className, info.name, info.descriptor);
//进行一下装箱操作，不然会包类型转换错误
box(info.type);
```
2、通过type获取opcode值
```java
/**
* 获取opcode
*
* @return 返回load opcode
*/
    public int getLoadCode() {
         return type.getOpcode(Opcodes.ILOAD);
    }

    /**
     * @return 返回store opcode
     */
    public int getStoreCode() {
        return type.getOpcode(Opcodes.ISTORE);
    }
```
# 调试技巧
1、查看class的字节码
```java
javap -c PrintField.class
```
2、查看complied class
运行字节码插桩工具后，可以在app/build/intermediates/transforms/目录下找到生成的class，但是class中的内容由于是compiled看不到，可以使用cfr工具支持反编译class文件和jar包
下载cfr的jar包，地址https://www.benf.org/other/cfr/,放到电脑的目录下，然后调用下面的命令就可以了
 反编译class文件：命令
java -jar  /Users/hanking/AndroidStudioProjects/Honno/cfr/cfr-0.151.jar  MainActivity.class
```java
public MainActivity() {
int n = LogCache.request();
long l = System.currentTimeMillis();
LogCache.updateMethodInfo(null, (String)"com/hank/hanno/MainActivity", (String)"<init>", (String)"()V", (long)l, (int)n);
LogCache.printMethodInfo((int)n, (int)3, (boolean)false, (String)"");
}
```
反编译jar包：命令
java -jar /Users/hanking/AndroidStudioProjects/Honno/cfr/cfr-0.151.jar --outputdir /tmp/outputdir

# 插桩之后生成的代码
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
# LogCache类
用于保存插桩过程中生成的数据，和相应的打印log的方法。
```java
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

  public static synchronized void printMethodInfo(int id, int logLevel, boolean enableTime, String tagName) {
  MethodInfo methodInfo = mCacheMethods.get(id);
  Printer.printMethodInfo(methodInfo, logLevel, enableTime, tagName, mCacheFields);
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
```
