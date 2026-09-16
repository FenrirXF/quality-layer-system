package com.quality;

import org.mindrot.jbcrypt.BCrypt;

//加密明文
public class PwdGenTest {
    public static void main(String[] args) {
        String raw = "123456";//输入明文
        String encrypt = BCrypt.hashpw(raw, BCrypt.gensalt());
        System.out.println("正确密文：" + encrypt);
        // 校验测试
        boolean check = BCrypt.checkpw(raw, encrypt);
        System.out.println("校验结果：" + check);
    }
}