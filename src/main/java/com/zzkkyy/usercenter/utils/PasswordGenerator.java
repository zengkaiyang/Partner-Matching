package com.zzkkyy.usercenter.utils;

import org.springframework.util.DigestUtils;

/**
 * 密码生成工具
 * 用于生成测试密码
 */
public class PasswordGenerator {
    
    private static final String SALT = "yupi";
    
    public static void main(String[] args) {
        String password = "123456";
        
        // 方式1：加盐加密（当前代码使用的方式）
        String encryptedWithSalt = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        System.out.println("=== 加盐加密 ===");
        System.out.println("原始密码: " + password);
        System.out.println("SALT: " + SALT);
        System.out.println("加密后: " + encryptedWithSalt);
        System.out.println();
        
        // 方式2：纯MD5（不加盐）
        String encryptedPure = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("=== 纯MD5加密 ===");
        System.out.println("原始密码: " + password);
        System.out.println("加密后: " + encryptedPure);
        System.out.println();
        
        // 生成SQL插入语句
        System.out.println("=== SQL插入语句（加盐） ===");
        System.out.println("INSERT INTO third_party_account (user_id, platform, account, password) VALUES");
        System.out.println("(1, 'wechat', 'test_wechat', '" + encryptedWithSalt + "'),");
        System.out.println("(1, 'qq', 'test_qq', '" + encryptedWithSalt + "');");
        System.out.println();
        
        System.out.println("=== SQL插入语句（纯MD5） ===");
        System.out.println("INSERT INTO third_party_account (user_id, platform, account, password) VALUES");
        System.out.println("(1, 'wechat', 'test_wechat', '" + encryptedPure + "'),");
        System.out.println("(1, 'qq', 'test_qq', '" + encryptedPure + "');");
    }
}
