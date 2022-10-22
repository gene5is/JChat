package com.Utils;




import com.Users.User;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Register {
    private boolean status=false;
    private User user=new User();
    private String driver="com.mysql.cj.jdbc.Driver";
    private String url="jdbc:mysql://localhost:3306/jchat?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false";
    private String dbcuser="root";
    private String password="root";

    public Register(User user){
        this.user=user;
    }

    public boolean Judge() {
        if(user.getUserName().equals("")){
            JOptionPane.showMessageDialog(null,"用户名不能为空！","用户名为空",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(user.getPassword().equals("")){
            JOptionPane.showMessageDialog(null,"密码不能为空！","密码为空",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(user.getNickName().equals("")){
            JOptionPane.showMessageDialog(null,"昵称不能为空","昵称为空",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean addUser() throws ClassNotFoundException {
        String sql="insert into users(nickname,username,password) values(?,?,?)";
        Class.forName(driver);
        //System.out.println(driver);
        try{
            Connection conn= DriverManager.getConnection(url,dbcuser,password);
            PreparedStatement ps=conn.prepareStatement(sql);
            Statement stmt = conn.createStatement();
            ps.setString(1,user.getNickName());
            ps.setString(2,user.getUserName());
            ps.setString(3,user.getPassword());
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"注册失败(该用户已存在)，请重试");
            this.setStatus(false);
            return false;
        }
        setStatus(true);
        return true;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}