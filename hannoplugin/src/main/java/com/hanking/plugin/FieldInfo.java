package com.hanking.plugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 用于存放 field相关信息
 */
public class FieldInfo {
    /**
     * 描述信息
     */
    String descriptor;
    int access;
    Type type;
    String name;

    FieldInfo(String descriptor, int access, Type type, String name) {
        this.descriptor = descriptor;
        this.access = access;
        this.type = type;
        this.name = name;
    }

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
}
