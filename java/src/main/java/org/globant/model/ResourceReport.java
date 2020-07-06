package org.globant.model;

import com.google.gson.Gson;
import org.globant.enums.TypesAws;

import java.util.List;
import java.util.stream.Collectors;

public class ResourceReport {

    private String type;
    private String resourceName;
    private List<ReportTag> tags;
    private List<String> missingTags;
    private String created;

    public ResourceReport(String type, String resourceName){
        this.type = type;
        this.resourceName = resourceName;
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

    public List<ReportTag> getTags() {
        return tags;
    }

    public String getStringTags() {
        if (tags == null) {
            return "";
        }
        return tags.stream()
                .map(ReportTag::getKey)
                .collect(Collectors.joining(","));
    }

    public void setTags(List<ReportTag> tags) {
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
