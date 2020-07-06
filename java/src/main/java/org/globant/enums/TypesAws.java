package org.globant.enums;

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
