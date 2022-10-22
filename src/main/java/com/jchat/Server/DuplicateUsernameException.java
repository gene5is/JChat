package com.jchat.Server;



public class DuplicateUsernameException extends Exception{
    public DuplicateUsernameException(String message){
        super(message);
    }
}
