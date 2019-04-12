package com.reem.halamish.androidtaapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PersonRecyclerUtils.PersonClickCallback {

    // check!

    private PersonRecyclerUtils.PersonAdapter adapter
            = new PersonRecyclerUtils.PersonAdapter();

    // check
    private ArrayList<Person> people = new ArrayList<>(Person.getAll());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.person_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
        adapter.callback = this;

        adapter.submitList(people);
    }

    @Override
    public void onPersonClick(Person person) {
        ArrayList<Person> peopleCopy = new ArrayList<>(this.people);
        peopleCopy.remove(person);
        this.people = peopleCopy;
        this.adapter.submitList(this.people);
    }
}
