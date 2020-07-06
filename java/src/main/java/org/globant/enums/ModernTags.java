package org.globant.enums;

public enum ModernTags {
    BU_MOD("rlg:business-unit"),
    PRODUCT( "rlg:product"),
    APPLICATION( "rlg:application"),
    REPOSITORY( "rlg:repository"),
    TEAM( "rlg:techdata-team"),
    CONTACT( "rlg:contact"),
    ENV( "rlg:environment"),
    CLASS( "rlg:classification"),
    COMP( "rlg:compliance");

    private final String value;

    ModernTags(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
