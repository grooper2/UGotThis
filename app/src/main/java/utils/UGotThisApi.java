package utils;

import android.app.Application;

public class UGotThisApi extends Application{

    private String username;
    private String userId;
    private static UGotThisApi instance;

    public static UGotThisApi getInstance(){
        if ( instance == null)
            instance = new UGotThisApi();
        return instance;

    }

    public UGotThisApi(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
