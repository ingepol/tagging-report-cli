package org.globant.model;

public class ReportTag {
    private String Key;
    private String Value;

    public ReportTag(String key, String value) {
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
        if (!(obj instanceof ReportTag)) return false;
        ReportTag o = (ReportTag) obj;
        return o.getKey() == this.getKey();
    }
}
