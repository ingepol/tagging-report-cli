package org.globant.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.globant.aws.CloudFormation;
import org.globant.busniess.TagsBusniess;
import org.globant.cli.convert.RegionConvert;
import org.globant.cli.convert.TypeConvert;
import org.globant.cli.parameters.ParametersCLI;
import org.globant.cli.validate.ValidateRegion;
import org.globant.cli.validate.ValidateType;
import org.globant.enums.TypesAws;
import org.globant.factory.ServiceFactory;
import org.globant.model.ResourceReport;
import org.globant.model.TagReport;
import org.globant.services.IService;
import org.globant.services.RegionService;
import org.globant.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudformation.model.Stack;

import java.util.ArrayList;
import java.util.List;

import static org.globant.enums.TypesAws.STACK;

class AppCLI {

    final static ParametersCLI params = new ParametersCLI();
    private static final Logger LOG = LoggerFactory.getLogger(RegionService.class);


    public static void main(String ... argv) {
        AppCLI main = new AppCLI();
        JCommander.newBuilder()
                .addObject(params)
                .build()
                .parse(argv);
        main.run();
    }

    public void run() {
        List<ResourceReport> resources = getResourcesReport();
        printReport(resources);
    }

    private List<ResourceReport> getResourcesReport(){

        List<ResourceReport> resources = new ArrayList<>();
        TagsBusniess tagsBusniess = new TagsBusniess();
        RegionService.createInstance(params.getRegion());

        if (params.getType().equals(STACK)) {
            LOG.info("Getting stacks...");
            CloudFormation cloudformation = new CloudFormation();
            List<Stack> stackSet = cloudformation.listStacks(params.getSearch());
            resources = cloudformation.getAllStackResources(stackSet);
        } else {
            IService awsService = ServiceFactory.getService(params.getType());
            if (awsService != null) {
                try {
                    resources = awsService.getAllResource();
                } catch (UnsupportedOperationException uoe){
                    LOG.error("Unsoported operation getAllResource for type " +
                            params.getType().getValue());
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

        return resources;
    }

    private void printReport(List<ResourceReport> resources){
        if (resources.size() > 0) {
            Utils.writeCSV(resources);
        } else {
            LOG.warn("The process didn't found resources. "
                    + "Try with other resource type or change the seatch parameter");
        }
    }
}

