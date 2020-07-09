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

import static org.globant.enums.TypesAws.*;


public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        TypesAws type = TypesAws.fromVale(args[0]);
        String filter = args.length > 1 ? args[1]:"";
        if (type == null){
            LOG.error("The type doesn't exist or it's not implemented, yet");
            System.exit(0);
        }
        LOG.info("Type: " + type.getValue());
        if (!filter.isEmpty())
            LOG.info("Filter: " + filter);

        List<ResourceReport> resources = new ArrayList<>();
        TagsBusniess tagsBusniess = new TagsBusniess();

        if (type.equals(STACK)) {
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
                List<String> missingTags = tagsBusniess.getMissingModernTags(tags);
                resource.setTags(requiredTags);
                resource.setMissingTags(missingTags);

                resource.setClassic(tagsBusniess.getClassicCoverage(tags));
                resource.setModern(tagsBusniess.getModernCoverage(tags));
            }
        }
        if (resources.size() > 0) {
            Utils.writeCSV(resources);
        }
    }
}
