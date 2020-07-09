package org.globant.model;

public class TagReport {
    private String Key;
    private String Value;

    public TagReport(String key, String value) {
        this.Key = key;
        this.Value = value;
    }

    public String getKey() {
        return Key;
    }

    public String getValue() {
        return Value;
    }


    @Override
    public String toString() {
        return "{" +
                "Key='" + Key + '\'' +
                ", Value='" + Value + '\'' +
                '}';
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof TagReport)) return false;
        TagReport o = (TagReport) obj;
        return o.getKey() == this.getKey();
    }
}
