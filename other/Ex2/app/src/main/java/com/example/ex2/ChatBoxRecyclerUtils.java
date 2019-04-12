package com.example.ex2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



public class ChatBoxRecyclerUtils {

    // check
    static class ChatBoxCallback
            extends DiffUtil.ItemCallback<ChatBox> {

        @Override
        public boolean areItemsTheSame(@NonNull ChatBox p1, @NonNull ChatBox p2) {
            return p1.text.equals(p2.text);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatBox p1, @NonNull ChatBox p2) {
            return p1.equals(p2);
        }
    }



    interface ChatBoxClickCallback {
        void onChatBoxClick(ChatBox ChatBox);
    }


    // check

    static class ChatBoxAdapter extends ListAdapter<ChatBox, ChatBoxHolder> {

        public ChatBoxAdapter() {
            super(new ChatBoxCallback());
        }

        public ChatBoxClickCallback callback;

        @NonNull @Override
        public ChatBoxHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            View itemView =
                    LayoutInflater.from(context)
                            .inflate(R.layout.chat_box_item, parent, false);
            final ChatBoxHolder holder = new ChatBoxHolder(itemView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatBox ChatBox = getItem(holder.getAdapterPosition());
                    if (callback != null)
                        callback.onChatBoxClick(ChatBox);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatBoxHolder ChatBoxHolder, int position) {
            ChatBox ChatBox = getItem(position);
            ChatBoxHolder.text.setText(ChatBox.text);
        }
    }

    // check!

    static class ChatBoxHolder
            extends RecyclerView.ViewHolder {

        public final TextView text;
        public ChatBoxHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textView);
        }
    }
}
