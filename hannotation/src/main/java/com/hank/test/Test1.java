package com.hank.test;

import com.hank.utils.ASMPrint;

import java.io.IOException;

public class Test1 {
    public static void main(String[] args) {
        try {
            ASMPrint.printAsm("com.hank.hannotation.LogCache");
//..            PrintASMCodeTree.print("com.hank.test.PrintField");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
