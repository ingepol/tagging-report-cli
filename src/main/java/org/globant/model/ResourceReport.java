package org.globant.model;

import org.apache.commons.lang3.StringUtils;
import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceReport {
    private final String id;
    private final TypesAws type;
    private final CreatedBy create;
    private String name;
    private Integer classic;
    private Integer modern;
    private List<TagReport> tags = Collections.emptyList();
    private List<String> missingTags = Collections.emptyList();

    public String getStringTags() {
        if (tags == null) {
            return "";
        }
        return tags.stream()
                .map(TagReport::getKey)
                .collect(Collectors.joining(","));
    }

    public String getStringMissingTags() {
        if (null == missingTags) {
            return "";
        }
        return String.join(",", missingTags);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TypesAws getType() {
        return type;
    }

    public CreatedBy getCreate() {
        return create;
    }

    public Integer getClassic() {
        return classic;
    }

    public Integer getModern() {
        return modern;
    }

    private ResourceReport(ResourceReport.Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.create = builder.create;
    }

    public static ResourceReport.Builder builder(String id) {
        return new ResourceReport.Builder(id);
    }

    public static class Builder {
        private final String id;
        private String name;
        private TypesAws type;
        private CreatedBy create;

        public Builder(String id) {
            this.id = id;
            this.name = id;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }


        public Builder withType(TypesAws type) {
            this.type = type;
            return this;
        }

        public Builder withCreate(CreatedBy created) {
            this.create = created;
            return this;
        }

        public Builder withResource(ResourceReport resource) {
            this.name = resource.name;
            this.type = resource.type;
            this.create = resource.create;
            return this;
        }

        public ResourceReport build() {
            return new ResourceReport(this);
        }

    }
}
