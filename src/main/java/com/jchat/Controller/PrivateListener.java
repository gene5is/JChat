package com.jchat.Controller;

import com.Users.Message;
import com.Users.MessageType;
import com.Users.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
//监听器
public class PrivateListener implements  Runnable {
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
    Logger logger=LoggerFactory.getLogger(com.jchat.demo.Listener.class);
    private static MessageType messageType=MessageType.PRIVATECHAT;
    private static String destination;

    public PrivateListener(String hostname, int port, String username, String destination,PrivateChatController controller){
        this.hostname=hostname;
        this.port=port;
        this.username=username;
        this.chatController=controller;
        this.destination=destination;
    }

    @Override
    public void run() {
        try{
            socket=new Socket(hostname,port);
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
            pricateConnect();
            logger.info("Sockets in and out ready");
            while(socket.isConnected()){

                Message message=(Message)objectInputStream.readObject();

                if(message!=null){
                    logger.debug("Message recieved:"+message.getMsg()+" MessageType:"+message.getType());
                    switch (message.getType()) {
                        /*case SERVER:
                            chatController.addAsServer(message);
                            break;*/
                        case PRIVATECHAT:
                            chatController.addToChat(message);
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //chatController.logoutScene();
        }
    }
    public static void send(String msg) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setDestination(destination);
        createMessage.setType(messageType);
        createMessage.setStatus(Status.AWAY);
        createMessage.setMsg(msg);
        System.out.println("["+msg+"]");
        System.out.println(createMessage);
        objectOutputStream.writeObject(createMessage);
        objectOutputStream.flush();
    }

    //发送私人连接请求
    public static void pricateConnect() throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setDestination(destination);
        System.out.println("destination:"+destination);
        createMessage.setType(MessageType.PRIVATECONNECT);
        createMessage.setMsg(HASCONNECTED);
        objectOutputStream.writeObject(createMessage);
    }
}
