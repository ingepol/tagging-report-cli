package org.globant.model;

import org.globant.enums.TypesAws;
import software.amazon.awssdk.regions.Region;

public class ParamsCLI {
    private TypesAws type;
    private String filter;
    private Region region;

    public TypesAws getType() {
        return type;
    }

    public void setType(TypesAws type) {
        this.type = type;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
