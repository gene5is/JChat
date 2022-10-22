package com.jchat.Server;

import com.Users.Message;
import com.Users.MessageType;
import com.Users.Status;
import com.Users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Server {
    private static final int PORT=1234;
    private static final HashMap<String,User> names=new HashMap<>();
    private static HashMap<String,ObjectOutputStream> writers=new HashMap<>();

    private static HashMap<String,ObjectOutputStream> privateChatWriters=new HashMap<>();
    private static ArrayList<User> users=new ArrayList<>();     //在线用户列表
    static  Logger logger= LoggerFactory.getLogger(Server.class);


    public static void main(String[] args) throws IOException {
        logger.info("The chat server is running");
        ServerSocket serverSocket=new ServerSocket(PORT);
        try{
            while(true){
                new Handler(serverSocket.accept()).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            serverSocket.close();
        }
    }

    private static class Handler extends Thread{
        private String name;
        private Socket socket;
        private Logger logger=LoggerFactory.getLogger(Handler.class);
        private User user;
        private ObjectInputStream  objectInputStream;
        private OutputStream outputStream;
        private ObjectOutputStream objectOutputStream;
        private InputStream inputStream;
        private MessageType messageType;

        public Handler(Socket socket) throws IOException{
            this.socket=socket;
        }
        public void  run(){
            logger.info("Attempting to connect a user...");
            try{
                inputStream=socket.getInputStream();
                objectInputStream=new ObjectInputStream(inputStream);
                outputStream=socket.getOutputStream();
                objectOutputStream=new ObjectOutputStream(outputStream);

                Message firstMessage=(Message) objectInputStream.readObject();
                System.out.println("firstMessage: "+firstMessage);
                if(firstMessage.getType()==MessageType.PRIVATECONNECT){
                    privateChatWriters.put(firstMessage.getName(),objectOutputStream);
                    messageType=MessageType.PRIVATECHAT;
                }
                else if(firstMessage.getType()!=MessageType.PRIVATECHAT){
                    checkDuplicateUsername(firstMessage);
                    messageType=MessageType.GROUPCHAT;
                    writers.put(firstMessage.getName(),objectOutputStream);
                    sendNotification(firstMessage);
                    addToList();
                }

                while(socket.isConnected()){
                    Message inputmsg=(Message)objectInputStream.readObject();
                    System.out.println("inputmsg:"+inputmsg);
                    logger.info(inputmsg.getType() + " - " + inputmsg.getName() + ": " + inputmsg.getMsg());
                    switch (inputmsg.getType()) {
                        case CONNECTED:
                            addToList();
                            break;
                        case STATUS:
                            changeStatus(inputmsg);
                            break;
                        case GROUPCHAT:
                            sendToGroup(inputmsg);
                            break;
                        case PRIVATECHAT:
                            sendToPerson(inputmsg, inputmsg.getDestination());
                            sendToPerson(inputmsg, inputmsg.getName());
                            break;
                        case PRIVATECONNECT:
                            privateConnected();
                            break;
                    }
                }

            } catch (SocketException socketException) {
                logger.error("Socket Exception for user " + name);
            } catch (DuplicateUsernameException duplicateException){
                logger.error("Duplicate Username : " + name);
            } catch (Exception e){
                logger.error("Exception in run() method for user: " + name, e);
            } finally {
                closedConnections();
            }
        }

        private void sendToGroup(Message msg) throws IOException {
            for(Map.Entry<String,ObjectOutputStream> mem:writers.entrySet()){
                mem.getValue().writeObject(msg);
                mem.getValue().reset();
            }
        }
        private void sendToPerson(Message msg,String destination) throws IOException {
            if(privateChatWriters.get(destination)==null){
                writers.get(destination).writeObject(msg);
                writers.get(destination).reset();
            }
            else{
                privateChatWriters.get(destination).writeObject(msg);
                privateChatWriters.get(destination).reset();
            }

        }

        //改变用户状态消息
        private Message changeStatus(Message inputmsg) throws IOException{
            logger.debug(inputmsg.getName() + " has changed status to  " + inputmsg.getStatus());
            Message msg = new Message();
            msg.setName(user.getUserName());
            msg.setType(MessageType.STATUS);
            msg.setMsg("");
            User userObj = names.get(name);
            userObj.setStatus(inputmsg.getStatus());
            sendToGroup(msg);
            return msg;
        }

        //用户名重复消息
        private synchronized void checkDuplicateUsername(Message firstMessage)throws DuplicateUsernameException{
            logger.info(firstMessage.getName()+"is trying to connect");
            if(!names.containsKey(firstMessage.getName())){
                this.name=firstMessage.getName();
                User user=new User();
                user.setUserName(firstMessage.getName());
                user.setStatus(Status.ONLINE);
                users.add(user);
                names.put(name,user);
                logger.info(name+" has been added to the list");
            }
            else {
                logger.error(firstMessage.getName()+ " is already connected");
                throw new DuplicateUsernameException(firstMessage.getName()+" is already connected");
            }
        }

        //通知消息
        private Message sendNotification(Message firstMessage) throws IOException{
            Message msg=new Message();
            msg.setMsg(" has joined the chat");
            msg.setType(MessageType.NOTIFICATION);
            msg.setName(firstMessage.getName());
            sendToGroup(msg);
            return msg;
        }

        //用户离开消息
        private Message removeFromList() throws IOException{
            logger.debug("removeFromList() method Enter");
            Message msg=new Message();
            msg.setMsg(" has left the chat.");
            msg.setType(MessageType.DISCONNECTED);
            msg.setName("SERVER");
            msg.setUserList(names);
            msg.setUsers(users);
            msg.setOnlineCount(names.size());
            sendToGroup(msg);
            logger.debug("removeFromList() method Exit");
            return msg;
        }

        private Message privateConnected() throws IOException {
            Message msg=new Message();
            msg.setType(MessageType.PRIVATECHAT);
            msg.setMsg("you have connected each other");
            sendToPerson(msg,msg.getName());
            return msg;
        }
        //用户加入列表消息
        private Message addToList() throws IOException{
            Message msg=new Message();
            msg.setMsg("Welcome, You have now joined the server!Enjoy chatting!");
            msg.setType(MessageType.CONNECTED);
            msg.setName("SERVER");
            msg.setUsers(users);
            msg.setUserList(names);
            msg.setOnlineCount(names.size());
            sendToGroup(msg);
            return msg;
        }
        //关闭客户端与服务器的连接
        private synchronized void closedConnections(){
            logger.debug("closeConnections() method Enter");
            logger.info("HashMap names:"+names.size()+"writers:"+writers.size()+" userList size:"+users.size());
            if(name!=null){
                names.remove(name);
                logger.info("User: "+name+" has been removed");
            }
            if (user != null){
                users.remove(user);
                logger.info("User object: " + user + " has been removed!");
            }
            if(objectOutputStream!=null){
                writers.remove(objectOutputStream);
                logger.info("Writer object: " + user + " has been removed!");
            }
            if(inputStream!=null){
                try{
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(outputStream!=null){
                try{
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(objectOutputStream!=null){
                try{
                    objectOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(objectInputStream!=null){
                try{
                    objectInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(writers.get(name)==null&&privateChatWriters.get(name)==null){
                try{
                    removeFromList();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            logger.info("HashMap names: " + names.size() + " writers:" + writers.size() + " usersList size:" + users.size());
            logger.debug("closeConnections() method Exit");
        }
    }
}

