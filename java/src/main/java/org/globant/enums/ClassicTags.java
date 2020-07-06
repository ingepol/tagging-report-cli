package org.globant.enums;

public enum ClassicTags {
    BU("BU"),
    NAME("Name"),
    PRODUCT("Product"),
    REPOSITORY("Repository"),
    TEAM("TeamID"),
    CONTACT("Environment");

    private final String Key;

    ClassicTags(String Key) {
        this.Key = Key;
    }

    public String getKey() {
        return Key;
    }
}
