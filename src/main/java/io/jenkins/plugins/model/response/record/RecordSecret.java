package io.jenkins.plugins.model.response.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordSecret {
    private String type;
    private String password;
    private String file;
    private String comment;

    @JsonProperty("$type")
    public String getType() {
        return type;
    }

    @JsonProperty("$type")
    public void setType(String value) {
        this.type = value;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String value) {
        this.password = value;
    }

    @JsonProperty("file")
    public String getFile() {
        return file;
    }

    @JsonProperty("file")
    public void setFile(String value) {
        this.file = value;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(String value) {
        this.comment = value;
    }
}
