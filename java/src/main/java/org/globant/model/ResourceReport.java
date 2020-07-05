package org.globant.model;

import com.google.gson.Gson;
import org.globant.enums.TypesAws;

import java.util.List;

public class ResourceReport {

    private String type;
    private String resourceName;
    private List<ReportTag> tags;
    private String missingTags;
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

    public String getJsonTags() {
        Gson gson = new Gson();
        return gson.toJson(tags);
    }

    public void setTags(List<ReportTag> tags) {
        this.tags = tags;
    }

    public String getMissingTags() {
        return missingTags;
    }

    public void setMissingTags(String missingTags) {
        this.missingTags = missingTags;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
