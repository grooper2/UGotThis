package model;

import com.google.firebase.Timestamp;

public class UGotThis {

    private String Id;
    private String title;
    private String ugotthis;
    private String imageUrl;
    private String discription;
    private String userId;
    private String status = "Unfinished";
    public static Timestamp timeAdded;
    private String userName;

    public UGotThis() {

    }

    public UGotThis(String title, String ugotthis, String imageUrl, String userId, Timestamp timeAdded, String userName, String discription, String status) {

        this.Id = Id;
        this.title = title;
        this.ugotthis = ugotthis;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.userName = userName;
        this.status = status;
        this.discription = discription;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }


    public String getUgotthis() {
        return ugotthis;
    }

    public void setUgotthis(String reflection) {
        this.ugotthis = reflection;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public  void  setStatus(String status){
        this.status = status;
    }


    public String getStatus() {
        return  status;
    }
}
