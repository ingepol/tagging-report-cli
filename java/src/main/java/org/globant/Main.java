package org.globant;

import org.globant.aws.Cloudformation;
import org.globant.busniess.TagsBusniess;
import org.globant.enums.TypesAws;
import org.globant.factory.ServiceFactory;
import org.globant.model.TagReport;
import org.globant.model.ResourceReport;
import org.globant.services.IService;
import org.globant.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudformation.model.Stack;

import java.util.ArrayList;
import java.util.List;


public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String type = args[0];
        String filter = args.length > 1 ? args[1]:"";
        LOG.info("Type: " + type);
        LOG.info(String.valueOf(type.equals(TypesAws.STACK.getValue())));
        LOG.info("Filter: " + filter);
        List<ResourceReport> resources = new ArrayList<ResourceReport>();
        TagsBusniess tagsBusniess = new TagsBusniess();

        if (type.equals(TypesAws.STACK.getValue())) {
            LOG.info("Getting stacks...");
            Cloudformation cloudformation = new Cloudformation();
            List<Stack> stackSet = cloudformation.listStacks(filter);
            resources = cloudformation.getAllStackResources(stackSet);
        } else {
            IService awsService = ServiceFactory.getService(type);
            if (awsService != null) {
                resources = awsService.getAllResource();
            }
        }

        for (ResourceReport resource: resources) {
            IService awsService = ServiceFactory.getService(resource.getType());
            if (awsService != null) {
                List<TagReport> tags = awsService.getTagResource(resource);

                List<TagReport> requiredTags = tagsBusniess.getAllTags(tags);
                List<String> missingTags = tagsBusniess.getMissingRequiredTags(tags);
                resource.setTags(requiredTags);
                resource.setMissingTags(missingTags);
            }
        }
        if (resources.size() > 0) {
            Utils.writeCSV(resources);
        }
    }
}
