package com.example.ex1;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;


class ChatBox implements Parcelable {
    private String text;

    public ChatBox(String text) {

        this.text = text;

    }

    private ChatBox(Parcel in) {
        text = in.readString();
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
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
        return Objects.equals(this.getText(),chatBox_obj.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}

