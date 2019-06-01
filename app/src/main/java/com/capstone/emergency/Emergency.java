package com.capstone.emergency;

public class Emergency {
    private String uID;
    private String eName;
    private String eLocation;

    public Emergency() {
        this.setuID(uID);
        this.seteName(eName);
        this.seteLocation(eLocation);
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String geteLocation() {
        return eLocation;
    }

    public void seteLocation(String eLocation) {
        this.eLocation = eLocation;
    }
}

