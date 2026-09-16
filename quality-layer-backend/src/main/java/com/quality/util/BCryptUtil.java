package com.quality.util;

import org.mindrot.jbcrypt.BCrypt;

//密码加密，禁止明文存储密码
public class BCryptUtil {
    /** 加密明文密码 */
    public static String encrypt(String rawPwd) {
        return BCrypt.hashpw(rawPwd, BCrypt.gensalt());
    }

    /** 比对明文与数据库密文 */
    public static boolean match(String rawPwd, String hashPwd) {
        return BCrypt.checkpw(rawPwd, hashPwd);
    }
}