package com.example.andrea.phonedirectory.model;

import java.io.Serializable;

/**
 * Created by andrea on 31/12/16.
 */
public class Contact implements Serializable {
    private Long id;
    private String name;
    private String surname;
    private String number;

    public Contact() {
    }

    public Contact(String name, String surname, String number) {
        this.name = name;
        this.surname = surname;
        this.number = number;
    }

    // determine whether at least one field of the Contact contains the given String
    public boolean contains(String searched) {
        return (this.name.toLowerCase().contains(searched) ||
                this.surname.toLowerCase().contains(searched) ||
                this.number.toLowerCase().contains(searched));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
