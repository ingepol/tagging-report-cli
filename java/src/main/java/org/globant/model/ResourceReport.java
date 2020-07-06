package org.globant.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceReport {

    private String type;
    private String resourceName;
    private String arn;
    private List<TagReport> tags = Collections.emptyList();
    private List<String> missingTags = Collections.emptyList();
    private String created;

    public ResourceReport(String type, String resourceName, String created){
        this.type = type;
        this.resourceName = resourceName;
        this.created = created;
    }

    public String getType() {
        return type;
    }

    public String getRequestType() {
        return type.split("::")[2];
    }

    public String getResourceName() {
        return resourceName;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
