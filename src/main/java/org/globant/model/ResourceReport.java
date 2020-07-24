package org.globant.model;

import org.globant.enums.CreatedBy;
import org.globant.enums.TypesAws;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceReport {
    private final TypesAws type;
    private final String name;
    private final String id;

    private List<TagReport> tags = Collections.emptyList();
    private List<String> missingTags = Collections.emptyList();
    private CreatedBy createdBy = CreatedBy.CUSTOM;
    private Integer classicCoverage;
    private Integer modernCoverage;

    private ResourceReport(TypesAws type, String name, String id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

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

    public TypesAws getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public Integer getClassicCoverage() {
        return classicCoverage;
    }

    public Integer getModernCoverage() {
        return modernCoverage;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public void setClassicCoverage(Integer classicCoverage) {
        this.classicCoverage = classicCoverage;
    }

    public void setModernCoverage(Integer modernCoverage) {
        this.modernCoverage = modernCoverage;
    }

    public void setTags(List<TagReport> tags) {
        this.tags = tags;
    }

    public void setMissingTags(List<String> missingTags) {
        this.missingTags = missingTags;
    }

    public static ResourceReport.ClassicBuilder classicBuilder() {
        return new ResourceReport.ClassicBuilder();
    }

    public static class ClassicBuilder {
        private String id;
        private String name;
        private TypesAws type;

        public ClassicBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ClassicBuilder withName(String name) {
            this.name = name;
            return this;
        }


        public ClassicBuilder withType(TypesAws type) {
            this.type = type;
            return this;
        }

        public ResourceReport build() {
            return new ResourceReport(this.type, this.name, this.id);
        }
    }

    public static Builder with(String region, String account) {
        return (type, name) -> new ResourceReport(
                    type,
                    name,
                    type.getId().apply(region).apply(account).apply(name)
            );
    }

    public interface Builder {
        ResourceReport build(TypesAws type, String name);
    }

    public static ResourceBuilder builder() {
        return region -> account -> type -> name -> new ResourceReport(
                type,
                name,
                type.getId().apply(region).apply(account).apply(name)
        );
    }

    public interface ResourceBuilder {
        ResourceReport.AddRegion withRegion(String region);
    }

    public interface AddRegion {
        ResourceReport.AddAccount withAccount(String account);
    }

    public interface AddAccount {
        ResourceReport.AddType withType(TypesAws type);
    }

    public interface AddType {
        ResourceReport withName(String name);
    }

    @Override
    public String toString() {
        return "ResourceReport{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", create=" + createdBy +
                ", classic=" + classicCoverage +
                ", modern=" + modernCoverage +
                ", tags=" + tags +
                ", missingTags=" + missingTags +
                '}';
    }
}
