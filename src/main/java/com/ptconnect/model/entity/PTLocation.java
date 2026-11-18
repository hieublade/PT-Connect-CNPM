package com.ptconnect.model.entity;

import java.io.Serializable;

public class PTLocation implements Serializable {
    private Long id;
    private Long ptId;
    private String locationName;

    public PTLocation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPtId() { return ptId; }
    public void setPtId(Long ptId) { this.ptId = ptId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
}
