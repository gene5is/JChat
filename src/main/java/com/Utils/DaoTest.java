package com.Utils;


import com.Users.User;

public class DaoTest {
    public static void main(String[] args) {
        User user=new User();
        user.setUserName("wjj");
        user.setPassword("123");
        UserDao dao=new UserDao();
        System.out.println(dao.login(user));
    }
}
