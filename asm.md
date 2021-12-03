//用于记录asm相关的知识点

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
odule打印log的能力