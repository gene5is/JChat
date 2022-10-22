package com.Users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    private String destination;
    private String name;
    private MessageType type;
    private String msg;
    private int count;
    private ArrayList<User> list;
    private ArrayList<User> users;
    private Status status;


    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getMsg(){
        return  msg;
    }
    public void setMsg(String msg){
        this.msg=msg;
    }

    public ArrayList<User> getUserList() {
        return list;
    }

    public void setUserList(ArrayList<User> list) {
        this.list = list;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
    public void setUserList(HashMap<String,User> userList){
        this.list=new ArrayList<>(userList.values());
    }

    public void setOnlineCount(int count){
        this.count=count;
    }

    public int getOnlineCount() {
        return count;
    }



    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Message{" +
                "destination='" + destination + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", msg='" + msg + '\'' +
                ", count=" + count +
                '}';
    }
}
