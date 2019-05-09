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
    public static int idCounter = 0;

    public ChatBox(String content, Timestamp timestamp) {

        this.content = content;
        this.id = ++idCounter;
        this.timestamp = timestamp;
    }

    public ChatBox(){

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

