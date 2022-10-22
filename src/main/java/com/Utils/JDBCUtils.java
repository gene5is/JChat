package com.Utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtils {
    private static DataSource ds;
    static {
        //加载配置文件
        Properties pro=new Properties();
        try{
            pro.load(JDBCUtils.class.getClassLoader().getResourceAsStream("druid.properties"));
            //初始化连接池对象
            ds= DruidDataSourceFactory.createDataSource(pro);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //获取连接池对象
    public static DataSource getSourse(){
        return ds;
    }
    //获取连接
    public static Connection getConnection() throws SQLException{
        return ds.getConnection();
    }
}
