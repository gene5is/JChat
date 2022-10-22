package com.jchat.demo;

import com.Users.Message;
import com.Users.MessageType;
import com.Users.Status;
import com.jchat.Controller.LoginController;
import com.jchat.Controller.PrivateChatController;
import com.jchat.Controller.UserListController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
//监听器
public class Listener implements  Runnable {
    private static final String HASCONNECTED = "has connected";
    private Socket socket;
    public String hostname;
    public int port;
    public static String username;
    public PrivateChatController chatController;
    public UserListController userListController;
    private InputStream inputStream;
    private OutputStream outputStream;
    private  static ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    Logger logger=LoggerFactory.getLogger(Listener.class);
    private static MessageType messageType=MessageType.GROUPCHAT;

    public Listener(String hostname, int port, String username, PrivateChatController controller,UserListController userListController){
        this.hostname=hostname;
        this.port=port;
        Listener.username=username;
        this.chatController=controller;
        this.userListController=userListController;
    }


    @Override
    public void run() {
        try{
            socket=new Socket(hostname,port);
            LoginController.getInstance();
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"未能连接到服务器");
            logger.error("连接失败");
        }
        logger.info("Connection accepted "+socket.getInetAddress()+":"+socket.getPort());

        try {
            if(messageType==MessageType.GROUPCHAT)
                connect();
            logger.info("Sockets in and out ready");
            while(socket.isConnected()){
                Message message=(Message)objectInputStream.readObject();
                System.out.println("Listener messsage Type:"+message.getType());
                if(message!=null){
                    logger.debug("Message recieved:"+message.getMsg()+" MessageType:"+message.getType());
                    switch (message.getType()) {
                        case CONNECTED:
                            userListController.setUserList(message);
                            break;
                        case DISCONNECTED:
                            userListController.setUserList(message);
                            break;
                        case STATUS:
                            userListController.setUserList(message);
                            break;
                        case PRIVATECHAT:
                            chatController.addToChat(message);
                            break;
                        case GROUPCHAT:
                            chatController.addToChat(message);
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public static void send(String msg) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(messageType);
        createMessage.setStatus(Status.AWAY);
        createMessage.setMsg(msg);
        System.out.println("["+msg+"]");
        System.out.println(createMessage);
        objectOutputStream.writeObject(createMessage);
        objectOutputStream.flush();
    }

    //发送连接请求
    public static void connect() throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.CONNECTED);
        createMessage.setMsg(HASCONNECTED);
        objectOutputStream.writeObject(createMessage);
    }
}
