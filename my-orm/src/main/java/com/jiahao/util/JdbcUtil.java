package com.jiahao.util;

import java.sql.*;
import java.util.List;

/**
 * jdbc工具类
 * @author JiaHao
 */
public class JdbcUtil {
    public static Connection conn = null;
    public static PreparedStatement stmt =null;
    public static Connection getConn(){
        Connection conn = null;
        try {
            Class.forName(PropertyUtil.getProperty("jdbc.driver"));
            conn = DriverManager.getConnection( PropertyUtil.getProperty("jdbc.url"),PropertyUtil.getProperty("jdbc.username"),PropertyUtil.getProperty("jdbc.password"));
        }catch(Exception e){
            System.out.println("获取连接对象失败.");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 增删改
     * @param sql 预编译SQL语句
     * @param params 参数
     * @return 受影响的记录数目
     */
    public static int excuteUpdate(String sql, Object[] params){
        Connection connection = null;
        PreparedStatement pstmt =null;
        int result = -1;
        try {
            connection = getConn();
            pstmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i ++){
                pstmt.setObject(i + 1, params[i]);
            }
            result = pstmt.executeUpdate();
            //获取id
            ResultSet resultSet = pstmt.getGeneratedKeys();
            if (resultSet.next()){
                result = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("更新数据出现异常.");
            System.out.println(e.getMessage());
        } finally {
            release(pstmt, connection);
        }
        return result;
    }

    /**
     * c查询的方法
     * @param sql
     * @param params
     * @return
     */
    public static ResultSet excuteSelect(String sql, Object[] params){
        Connection connection = null;
        PreparedStatement pstmt =null;
        ResultSet resultSet = null;
        try {
            connection = getConn();
            conn = connection;
            pstmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            stmt = pstmt;
            for (int i = 0; i < params.length; i ++){
                if (params[i] != null){
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            resultSet = pstmt.executeQuery();


        } catch (SQLException e) {
            System.out.println("更新数据出现异常.");
            System.out.println(e.getMessage());
        }
        return resultSet;
    }

    public static void release(){
        release(stmt,conn);
    }
    public static void release(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }
}
