package com.facebook;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;


public class Message extends Content implements Serializable {

    public Message(String text, String sender) {
       super(text,sender);
    }

    @Override
    public void Print_Content() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        System.out.println(getSender()+" : "+getText());
        System.out.println(getTime().format(formatter));
    }

}

