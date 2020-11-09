package io.jenkins.plugins.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    private Link[] links;
    private Permission[] permissions;
    private AdditionalObjects additionalObjects;
    private boolean admin;
    private String uuid;
    private String name;
    private boolean applicationAdministration;
    private boolean auditor;
    private boolean singleManaged;
    private boolean rotatingPasswordRequired;
    private String extendedAccess;
    private boolean recordTrail;

    @JsonProperty("links")
    public Link[] getLinks() {
        return links;
    }

    @JsonProperty("links")
    public void setLinks(Link[] value) {
        this.links = value;
    }

    @JsonProperty("permissions")
    public Permission[] getPermissions() {
        return permissions;
    }

    @JsonProperty("permissions")
    public void setPermissions(Permission[] value) {
        this.permissions = value;
    }

    @JsonProperty("additionalObjects")
    public AdditionalObjects getAdditionalObjects() {
        return additionalObjects;
    }

    @JsonProperty("additionalObjects")
    public void setAdditionalObjects(AdditionalObjects value) {
        this.additionalObjects = value;
    }

    @JsonProperty("admin")
    public boolean getAdmin() {
        return admin;
    }

    @JsonProperty("admin")
    public void setAdmin(boolean value) {
        this.admin = value;
    }

    @JsonProperty("uuid")
    public String getUUID() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUUID(String value) {
        this.uuid = value;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String value) {
        this.name = value;
    }

    @JsonProperty("applicationAdministration")
    public boolean getApplicationAdministration() {
        return applicationAdministration;
    }

    @JsonProperty("applicationAdministration")
    public void setApplicationAdministration(boolean value) {
        this.applicationAdministration = value;
    }

    @JsonProperty("auditor")
    public boolean getAuditor() {
        return auditor;
    }

    @JsonProperty("auditor")
    public void setAuditor(boolean value) {
        this.auditor = value;
    }

    @JsonProperty("singleManaged")
    public boolean getSingleManaged() {
        return singleManaged;
    }

    @JsonProperty("singleManaged")
    public void setSingleManaged(boolean value) {
        this.singleManaged = value;
    }

    @JsonProperty("rotatingPasswordRequired")
    public boolean getRotatingPasswordRequired() {
        return rotatingPasswordRequired;
    }

    @JsonProperty("rotatingPasswordRequired")
    public void setRotatingPasswordRequired(boolean value) {
        this.rotatingPasswordRequired = value;
    }

    @JsonProperty("extendedAccess")
    public String getExtendedAccess() {
        return extendedAccess;
    }

    @JsonProperty("extendedAccess")
    public void setExtendedAccess(String value) {
        this.extendedAccess = value;
    }

    @JsonProperty("recordTrail")
    public boolean getRecordTrail() {
        return recordTrail;
    }

    @JsonProperty("recordTrail")
    public void setRecordTrail(boolean value) {
        this.recordTrail = value;
    }
}
