package com.capstone.contact;

public class Contact {
    private String contactid;
    private String relationship;

    public Contact()
    {

    }

    public Contact(String contactid, String relationship) {
        this.setContactid(contactid);
        this.setRelationship(relationship);
    }

    public String getContactid() {
        return contactid;
    }

    public void setContactid(String contactid) {
        this.contactid = contactid;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
