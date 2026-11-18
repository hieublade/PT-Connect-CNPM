package com.ptconnect.model.entity;

public class PT extends User {
    private PTProfile ptProfile;

    public PT() {
        setRole("PT");
    }

    public PTProfile getPtProfile() { return ptProfile; }
    public void setPtProfile(PTProfile ptProfile) { this.ptProfile = ptProfile; }
}
