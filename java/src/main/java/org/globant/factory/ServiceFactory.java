package org.globant.factory;

import org.globant.Main;
import org.globant.enums.TypesAws;
import org.globant.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceFactory.class);

    public static IService getService(String serviceType) {
        if (serviceType == null) {
            return null;
        }
        IService awsService = null;
        if (serviceType.endsWith(TypesAws.PARAMETER.getValue())) {
            awsService = SSMService.getInstance();
        } else if (serviceType.endsWith(TypesAws.ROLE.getValue())) {
            awsService = RoleService.getInstance();
        } else if (serviceType.endsWith(TypesAws.LAMBDA.getValue())) {
            awsService = LambdaService.getInstance();
        } else if (serviceType.endsWith(TypesAws.TOPIC.getValue())) {
            awsService = TopicService.getInstance();
        }  else if (
                serviceType.endsWith(TypesAws.PORTAFOLIO.getValue()) ||
                        serviceType.endsWith(TypesAws.PRODUCT.getValue()) ||
                        serviceType.endsWith(TypesAws.DMS_SUBSCRIPTION.getValue())
        )
        {
            LOG.warn("The type: " + serviceType + " has not been impemented, yet") ;
        } else {
            LOG.warn("The type: " + serviceType + " don't support tagging") ;
        }
        return awsService;
    }

}