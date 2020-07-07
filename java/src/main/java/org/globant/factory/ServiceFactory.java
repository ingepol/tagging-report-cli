package org.globant.factory;

import org.globant.enums.TypesAws;
import org.globant.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.globant.enums.TypesAws.*;

public class ServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceFactory.class);

    public static IService getService(TypesAws serviceType) {
        if (serviceType == null) {
            return null;
        }
        IService awsService = null;
        if (serviceType.equals(PARAMETER)) {
            awsService = SSMService.getInstance();
        } else if (serviceType.equals(ROLE)) {
            awsService = RoleService.getInstance();
        } else if (serviceType.equals(LAMBDA)) {
            awsService = LambdaService.getInstance();
        } else if (serviceType.equals(TOPIC)) {
            awsService = TopicService.getInstance();
        } else if (serviceType.getKey().contains("DMS")) {
            awsService = DmsService.getInstance();
        } else if  (serviceType.getKey().contains("ServiceCatalog")) {
            awsService = ServiceCatalogService.getInstance();
        } else {
            LOG.warn("The type: " + serviceType + " don't support tagging") ;
        }
        return awsService;
    }

}