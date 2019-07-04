package com.capstone.contact;

public class Contact {
    private String id;
    private String name;
    private String number;
    private String relationship;

    public Contact()
    {
        //required
    }

    public Contact(String id, String name, String number, String relationship) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.relationship = relationship;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
