package com.zzkkyy.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Hive 连接测试
 */
@SpringBootTest
public class HiveConnectionTest {

    @Test
    public void testHiveConnection() {
        String hiveUrl = "jdbc:hive2://192.168.26.202:10000/default";
        String hiveUsername = "";
        String hivePassword = "";
        
        Connection conn = null;
        try {
            // 加载驱动
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            System.out.println("✅ Hive JDBC 驱动加载成功");
            
            // 创建连接
            Properties props = new Properties();
            if (hiveUsername != null && !hiveUsername.trim().isEmpty()) {
                props.setProperty("user", hiveUsername);
            }
            if (hivePassword != null) {
                props.setProperty("password", hivePassword);
            }
            
            System.out.println("正在连接 Hive: " + hiveUrl);
            conn = DriverManager.getConnection(hiveUrl, props);
            System.out.println("✅ Hive 连接成功");
            
            // 测试查询
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            
            System.out.println("\n📋 Hive 中的表:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            System.err.println("❌ Hive 连接失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("\n✅ Hive 连接已关闭");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
