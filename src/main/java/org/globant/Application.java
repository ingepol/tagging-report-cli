package org.globant;

import org.globant.aws.CloudFormation;
import org.globant.cli.CommandsCLI;
import org.globant.busniess.TagsBusniess;
import org.globant.factory.ServiceFactory;
import org.globant.model.ParamsCLI;
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


public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        List<ResourceReport> resources = new ArrayList<>();
        TagsBusniess tagsBusniess = new TagsBusniess();
        CommandsCLI commandsCLI = new CommandsCLI();
        ParamsCLI paramsCLI = commandsCLI.getParamsCLI(args);

        if (paramsCLI.getType().equals(STACK)) {
            LOG.info("Getting stacks...");
            CloudFormation cloudformation = new CloudFormation();
            List<Stack> stackSet = cloudformation.listStacks(paramsCLI.getFilter());
            resources = cloudformation.getAllStackResources(stackSet);
        } else {
            IService awsService = ServiceFactory.getService(paramsCLI.getType());
            if (awsService != null) {
                try {
                    resources = awsService.getAllResource();
                } catch (UnsupportedOperationException uoe){
                    LOG.error("Unsoported operation getAllResource for type " +
                            paramsCLI.getType().getValue());
                }
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
        } else {
            LOG.warn("The process didn't found resources. Try changing type or filter");
        }
    }

}
