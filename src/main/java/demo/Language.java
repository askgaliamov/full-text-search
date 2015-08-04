package demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Language {

    @JsonProperty("Name")
    private String name;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Designed by")
    private String designedBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesignedBy() {
        return designedBy;
    }

    public void setDesignedBy(String designedBy) {
        this.designedBy = designedBy;
    }

    @JsonIgnore
    public String getAll() {
        return (name + " " + type + " " + designedBy).replaceAll(",", " ");
    }

    @Override
    public String toString() {
        return "JSONSegment{" +
                "designedBy='" + designedBy + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
