package org.globant.enums;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public enum TypesAws {
    DMS_SUBSCRIPTION("AWS::DMS::EventSubscription", "EventSubscription", null),
    DMS_SUBNET_GROUP("AWS::DMS::ReplicationSubnetGroup", "SubnetGroup", null),
    DMS_TASK("AWS::DMS::ReplicationTask", "Task", null),
    DMS_ENDPOINT("AWS::DMS::Endpoint", "Endpoint", null),
    DMS_INSTANCE("AWS::DMS::ReplicationInstance", "Instance", null),
    LAMBDA("AWS::Lambda::Function", "Function", null),
    PARAMETER("AWS::SSM::Parameter", "Parameter", null),
    ROLE("AWS::IAM::Role", "Role", null),
    RULE("AWS::Events::Rule", "Rule", null),
    STACK("AWS::CloudFormation::Stack", "Stack", null),
    S3("AWS::S3::Bucket", "S3", null),
    TOPIC("AWS::SNS::Topic", "Topic", null),

    //Service Catalog
    PORTFOLIO("AWS::ServiceCatalog::Portfolio",             "Portfolio", ARN_AWS("catalog", "portfolio")),
    PRODUCT  ("AWS::ServiceCatalog::CloudFormationProduct", "Product",   ARN_AWS("catalog", "product")),

    //Glue Service
    DATABASE("AWS::Glue::Database", "Database", ARN_AWS("glue", "database")),
    CRAWLER ("AWS::Glue::Crawler",  "Crawler",  ARN_AWS("glue", "crawler")),
    JOB     ("AWS::Glue::Job",      "Job",      ARN_AWS("glue", "job")),
    TRIGGER ("AWS::Glue::Trigger",  "Trigger",  ARN_AWS("glue", "trigger")),
    ;

    private static Function<String, Function<String, Function<String, String>>> ARN_AWS(String service, String resource) {
        return region -> account -> name -> String.format("arn:aws:%s:%s:%s:%s/%s", service, region, account, resource, name);
    }

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

    public static TypesAws fromValue(String key) {
        for (TypesAws type : TypesAws.values()) {
            if (type.getValue().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return null;
    }

    private final String key;
    private final String value;
    private final Function<String, Function<String, Function<String, String>>> arn;

    TypesAws(String key, String value, Function<String, Function<String, Function<String, String>>> arn) {
        this.key = key;
        this.value = value;
        this.arn = arn;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    public Function<String, Function<String, Function<String, String>>> getArn() {
        return arn;
    }
}
