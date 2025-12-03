package com.facebook;
import java.io.Serializable;

public class Credentials implements Serializable {

    private String username;
    private String password;

    Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    boolean p_Verify(String password){
        return password.equals(this.password);
    }

}

