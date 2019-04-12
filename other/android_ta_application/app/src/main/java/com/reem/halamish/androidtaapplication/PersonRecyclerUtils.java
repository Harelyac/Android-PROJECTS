package com.reem.halamish.androidtaapplication;

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



public class PersonRecyclerUtils {

    // check
    static class PersonCallback
            extends DiffUtil.ItemCallback<Person> {

        @Override
        public boolean areItemsTheSame(@NonNull Person p1, @NonNull Person p2) {
            return p1.name.equals(p2.name);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Person p1, @NonNull Person p2) {
            return p1.equals(p2);
        }
    }



    interface PersonClickCallback {
        void onPersonClick(Person person);
    }


    // check

    static class PersonAdapter
            extends ListAdapter<Person, PersonHolder> {

        public PersonAdapter() {
            super(new PersonCallback());
        }

        public PersonClickCallback callback;

        @NonNull @Override
        public PersonHolder onCreateViewHolder(@NonNull ViewGroup parent, int itemType) {
            Context context = parent.getContext();
            View itemView =
                    LayoutInflater.from(context)
                            .inflate(R.layout.item_one_person, parent, false);
            final PersonHolder holder = new PersonHolder(itemView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Person person = getItem(holder.getAdapterPosition());
                    if (callback != null)
                        callback.onPersonClick(person);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull PersonHolder personHolder, int position) {
            Person person = getItem(position);
            personHolder.text.setText(person.name + ", age: " + person.age);
            if (person.isMale) {
                personHolder.image.setImageResource(R.drawable.male);
            } else {
                personHolder.image.setImageResource(R.drawable.female);
            }
        }
    }

    // check!

    static class PersonHolder
            extends RecyclerView.ViewHolder {

        public final ImageView image;
        public final TextView text;
        public PersonHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.person_image);
            text = itemView.findViewById(R.id.person_text);
        }
    }
}
