package org.globant.model;

import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceReport {

    private TypesAws type;
    private String resourceName;
    private String arn;
    private List<TagReport> tags = Collections.emptyList();
    private List<String> missingTags = Collections.emptyList();
    private CreatedBy created;
    private Integer classic;
    private Integer modern;

    public ResourceReport(TypesAws type, CreatedBy created){
        this.type = type;
        this.created = created;
    };
    public ResourceReport(TypesAws type, String resourceName, CreatedBy created){
        this.type = type;
        this.resourceName = resourceName;
        this.created = created;
    }

    public TypesAws getType() {
        return type;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public CreatedBy getCreated() {
        return created;
    }

    public List<TagReport> getTags() {
        return tags;
    }

    public String getStringTags() {
        if (tags == null) {
            return "";
        }
        return tags.stream()
                .map(TagReport::getKey)
                .collect(Collectors.joining(","));
    }

    public String getArn() {
        return arn;
    }

    public void setArn(String arn) {
        this.arn = arn;
    }

    public void setTags(List<TagReport> tags) {
        this.tags = tags;
    }

    public List<String> getMissingTags() {
        return missingTags;
    }

    public String getStringMissingTags() {
        if (missingTags == null) {
            return "";
        }
        return missingTags.stream()
                .collect(Collectors.joining(","));
    }

    public void setMissingTags(List<String> missingTags) {
        this.missingTags = missingTags;
    }

    public void setType(TypesAws type) {
        this.type = type;
    }

    public void setCreated(CreatedBy created) {
        this.created = created;
    }

    public Integer getClassic() {
        return classic;
    }

    public void setClassic(Integer classic) {
        this.classic = classic;
    }

    public Integer getModern() {
        return modern;
    }

    public void setModern(Integer modern) {
        this.modern = modern;
    }

    @Override
    public String toString() {
        return "ResourceReport{" +
                "type=" + type +
                ", resourceName='" + resourceName + '\'' +
                ", arn='" + arn + '\'' +
                ", tags=" + tags +
                ", missingTags=" + missingTags +
                ", created=" + created +
                ", classic=" + classic +
                ", modern=" + modern +
                '}';
    }
}
