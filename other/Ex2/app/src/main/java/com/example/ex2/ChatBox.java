package com.example.ex2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


class ChatBox {

    public static int color;
    public static String text;
    public static String sender;
    public static String reciever;


    private ChatBox(int color,
                    String text,
                    String sender,
                    String reciever) {

        this.color = color;
        this.text = text;
        this.sender = sender;
        this.reciever = reciever;
    }

    static List<ChatBox> getAll() {
        ArrayList<ChatBox> all = new ArrayList<>();
        all.add(new ChatBox(1, "simple_text", "harel", reciever = "harel"));
        all.add(new ChatBox(1, "simple_text", "harel", reciever = "harel"));
        all.add(new ChatBox(0, "simple_text", "harel", reciever = "harel"));
        all.add(new ChatBox(1, "simple_text", "harel", reciever = "harel"));
        return all;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatBox chatBox = (ChatBox) o;
        return color == chatBox.color &&
                sender.equals(chatBox.sender) &&
                reciever.equals(chatBox.reciever);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, sender, reciever);
    }
}
