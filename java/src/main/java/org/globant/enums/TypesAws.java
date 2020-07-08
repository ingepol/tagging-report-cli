package org.globant.enums;

import java.util.HashSet;
import java.util.Set;

public enum TypesAws {
    DMS_SUBSCRIPTION("AWS::DMS::EventSubscription", "EventSubscription"),
    DMS_SUBNET_GROUP("AWS::DMS::ReplicationSubnetGroup", "SubnetGroup"),
    DMS_TASK("AWS::DMS::ReplicationTask", "Task"),
    DMS_ENDPOINT("AWS::DMS::Endpoint", "Endpoint"),
    DMS_INSTANCE("AWS::DMS::ReplicationInstance", "Instance"),
    LAMBDA("AWS::Lambda::Function", "Function"),
    PARAMETER("AWS::SSM::Parameter", "Parameter"),
    PORTAFOLIO("AWS::ServiceCatalog::Portfolio","Portfolio"),
    PRODUCT("AWS::ServiceCatalog::CloudFormationProduct","Product"),
    ROLE("AWS::IAM::Role","Role"),
    STACK("AWS::CloudFormation::Stack", "Stack"),
    TOPIC("AWS::SNS::Topic", "Topic");

    private static final Set<String> KEYS = new HashSet<>();
    private static final Set<String> VALUES = new HashSet<>();

    static {
        for(TypesAws typeAws: TypesAws.values()) {
            KEYS.add(typeAws.getKey());
        }
        for(TypesAws typeAws: TypesAws.values()) {
            VALUES.add(typeAws.getValue());
        }
    }

    public static boolean hasKey(String key) {
        return KEYS.contains(key);
    }

    public static boolean hasValue(String value) {
        return VALUES.contains(value);
    }

    public static TypesAws fromKey(String key) {
        for (TypesAws type : TypesAws.values()) {
            if (type.getKey().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return null;
    }

    public static TypesAws fromVale(String key) {
        for (TypesAws type : TypesAws.values()) {
            if (type.getValue().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return null;
    }

    private final String key;
    private final String value;

    TypesAws(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
}
