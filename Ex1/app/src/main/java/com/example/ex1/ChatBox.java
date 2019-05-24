package com.example.ex1;
import android.os.Parcel;
import android.os.Parcelable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;


public class ChatBox implements Parcelable {
    private String content;
    private int id;
    private Date timestamp;
    private String model;
    private String manufactor;
    public static int idCounter = 0;


    public ChatBox(String content, Timestamp timestamp,String model, String manufactor) {
        this.content = content;
        this.id = ++idCounter;
        this.timestamp = timestamp;
        this.model = model;
        this.manufactor = manufactor;
    }


    public String getModel() {
        return model;
    }


    public String getManufactor() {
        return manufactor;
    }

    public ChatBox(){

    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }


    public Date getTimestamp() {
        return timestamp;
    }


    private ChatBox(Parcel in) {
        content = in.readString();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
    }

    public static final Parcelable.Creator<ChatBox> CREATOR = new Parcelable.Creator<ChatBox>() {
        @Override
        public ChatBox createFromParcel(Parcel in) {
            return new ChatBox(in);
        }

        @Override
        public ChatBox[] newArray(int size) {
            return new ChatBox[size];
        }
    };


    @Override
    public boolean equals( Object obj) {
        if(this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass() ) return false;
        ChatBox chatBox_obj = (ChatBox)obj;
        return Objects.equals(this.getContent(),chatBox_obj.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}

