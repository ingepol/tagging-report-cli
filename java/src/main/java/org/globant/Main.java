package org.globant;

import org.globant.aws.Cloudformation;
import org.globant.enums.TypesAws;
import org.globant.factory.ServiceFactory;
import org.globant.model.ReportTag;
import org.globant.model.ResourceReport;
import org.globant.services.IService;
import org.globant.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;


public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String type = args[0];
        String filter = args[1];
        LOG.info("Type: " + type);
        LOG.info(String.valueOf(type.equals(TypesAws.STACK.getValue())));
        LOG.info("Filter: " + filter);
        List<ResourceReport> resources = new ArrayList<ResourceReport>();
        if (type.equals(TypesAws.STACK.getValue())) {
            LOG.info("Getting stacks...");
            Cloudformation cloudformation = new Cloudformation();
            List<Stack> stackSet = cloudformation.listStacks(filter);
            resources = cloudformation.getAllStackResources(stackSet);
        }

        for (ResourceReport resource: resources) {
            try {
                IService awsService = ServiceFactory.getService(resource.getType());
                if (awsService != null) {
                    List<ReportTag> tags = awsService.getTagResource(resource);
                    resource.setTags(tags);
                }
            } catch (NotImplementedException isx){
                LOG.warn("The type " + resource.getType() + " has not implemented, yet");
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
        }


        Utils.writeCSV(resources);
    }
}
