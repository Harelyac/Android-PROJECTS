package com.reem.halamish.androidtaapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


class Person {
    static List<Person> getAll() {

            ArrayList<Person> all = new ArrayList<>();
            all.add(new Person(50, true, "Homer Simpson"));
            all.add(new Person(47, false, "Marge Simpson"));
            all.add(new Person(13, true, "Bart Simpson"));
            all.add(new Person(15, false, "Lissa Simpson"));
            return all;
        }

    final int age;
    final boolean isMale;
    public final String name;

    private Person(int age,
                   boolean isMale,
                   String name) {

        this.age = age;
        this.isMale = isMale;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age &&
                isMale == person.isMale &&
                Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, isMale, name);
    }
}
