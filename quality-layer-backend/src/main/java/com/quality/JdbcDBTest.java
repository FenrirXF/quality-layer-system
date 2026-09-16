package com.quality;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//数据库连接测试
public class JdbcDBTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/quality_layer?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowMultiQueries=true&allowPublicKeyRetrieval=true&useSSL=false";
        String username = "root";
        String password = "20070809XXFxxf";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ 数据库连接成功！MySQL服务、账号密码、库名全部正常");
        } catch (SQLException e) {
            System.err.println("❌ 数据库连接失败！");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}