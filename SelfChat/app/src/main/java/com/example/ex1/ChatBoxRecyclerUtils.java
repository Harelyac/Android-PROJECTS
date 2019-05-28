package com.example.ex1;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class ChatBoxRecyclerUtils {

    static class ChatBoxCallback
            extends DiffUtil.ItemCallback<ChatBox> {

        @Override
        public boolean areItemsTheSame(@NonNull ChatBox cb1, @NonNull ChatBox cb2) {
            return cb1.equals(cb2);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatBox cb1, @NonNull ChatBox cb2) {
            return cb1.getContent().equals(cb2.getContent());
        }
    }


    interface ChatBoxClickCallback {
        void onChatBoxClick(ChatBox ChatBox);
    }


    static class ChatBoxAdapter extends ListAdapter<ChatBox, ChatBoxHolder> {

        public MainActivity callback;

        public ChatBoxAdapter() {
            super(new ChatBoxCallback());
        }

        // public ChatBoxClickCallback callback;

        @NonNull @Override
        public ChatBoxHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.chat_box_item, parent, false);
            final ChatBoxHolder holder = new ChatBoxHolder(itemView);

            // listener to view holder
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ChatBox ChatBox = getItem(holder.getAdapterPosition());
                    if (callback != null) {
                        callback.onChatBoxClick(ChatBox);
                        return true;
                    }
                    return false;
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatBoxHolder ChatBoxHolder, int position) {
            ChatBox ChatBox = getItem(position);
            ChatBoxHolder.text.setText(ChatBox.getContent());
        }
    }


    static class ChatBoxHolder extends RecyclerView.ViewHolder {
        public final TextView text;
        public ChatBoxHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textView);
        }
    }
}
