package com.briup.common;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库操作类
 */
public class DBUtils {
    private static DBUtils dbu;
    private static Properties  properties;

//    private DBUtils(){}
//
//    public static DBUtils getInstance(){
//        if(dbu==null){
//            synchronized (DBUtils.class){
//                if (dbu == null) {
//                    dbu = new DBUtils();
//                }
//            }
//        }
//        return dbu;
//    }

    static {
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("db.properties"));
            Class.forName(properties.getProperty("driver"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接对象
     * @return SQL的connection对象
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("url");
        return DriverManager.getConnection(url,properties);
    }

    /**
     * 关闭资源
     * @param resultSet
     * @param statement
     * @param connection
     */
    public static void close(ResultSet resultSet, Statement statement, Connection connection){
        try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    //增删改操作

    /**
     * 增删改操作集成方法
     * @param sql sql语句
     * @param params 参数
     * @return 影响行数
     * @throws SQLException
     */
    public static int update(String sql,Object...params) throws SQLException {
        int num = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            if(params.length>0){
                for (int i = 0; i < params.length; i++){
                    ps.setObject(i+1,params[i]);
                }
            }
            num = ps.executeUpdate();
        } finally {
            close(null,ps,connection);
        }

        return num;
    }

    /**
     * 查询方法
     * @param sql SQL方法
     * @param params 查询的参数
     * @return 缓存的结果集
     * @throws SQLException
     */
    public static CachedRowSet query(String sql,Object...params) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CachedRowSet crs = new CachedRowSetImpl();
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            if(params.length>0){
                for (int i = 0; i < params.length; i++){
                    ps.setObject(i+1,params[i]);
                }
            }
            rs = ps.executeQuery();
            crs.populate(rs);
        } finally {
            close(rs,ps,connection);
        }

        return crs;
    }


}
