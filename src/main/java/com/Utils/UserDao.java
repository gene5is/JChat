package com.Utils;

import com.Users.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.swing.*;

//操作数据库中的user表的对象
public class UserDao {
    //创建jdbcTemplate对象，用来操作数据库
    JdbcTemplate template=new JdbcTemplate(JDBCUtils.getSourse());
    public User login(User loginUser){
        try{
            String sql="select * from users where username = ?";
            User user =template.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<User>(User.class),
                    loginUser.getUserName()
            );
            if(user.getPassword().equals(loginUser.getPassword()))
                return user;
            else {
                JOptionPane.showMessageDialog(null,"密码错误，请重新输入");
            }
        }catch (DataAccessException e){
            e.printStackTrace();
            return null;
        }
        return  null;
    }
}
