package controller;

import java.util.regex.Pattern;

public class AccountControl {


    public void register(String username, String password, String type){
        if(/*is there username with name*/ true){

        } else if(!isPasswordValid(password)){

        } else {

        }
    }

    public void login(String username, String password){
        if(/*is there username with name*/ true){

        } else if(/*is password correct*/ true){

        } else {

        }
    }

    public boolean isPasswordValid(String password){
        if(password.length() > 16 || password.length() < 8 || Pattern.matches(password, "^[A-Za-z0-9]+$"))
            return false;

        return true;
    }

}
